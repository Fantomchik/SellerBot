package ru.sellerbot.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sellerbot.config.ManagerConfig;
import ru.sellerbot.config.PriceDefaultsConfig;
import ru.sellerbot.dto.PhoneInfoDto;
import ru.sellerbot.persistence.entity.PhonePriceEntity;
import ru.sellerbot.persistence.entity.PriceRequestEntity;
import ru.sellerbot.persistence.entity.PriceRequestStatus;
import ru.sellerbot.persistence.repository.PhonePriceRepository;
import ru.sellerbot.persistence.repository.PriceRequestRepository;
import ru.sellerbot.persistence.repository.PriceRequestWaiterRepository;
import ru.sellerbot.service.ManagerPriceReplyService;
import ru.sellerbot.service.TelegramAsyncMessageSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerPriceReplyServiceImpl implements ManagerPriceReplyService {

    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d[\\d\\s]{2,})");

    private final ManagerConfig managerConfig;
    private final PriceDefaultsConfig priceDefaultsConfig;
    private final PriceRequestRepository priceRequestRepository;
    private final PriceRequestWaiterRepository priceRequestWaiterRepository;
    private final PhonePriceRepository phonePriceRepository;
    private final TelegramAsyncMessageSender telegramAsyncMessageSender;

    @Override
    public boolean canHandle(Message message) {
        if (message == null || !message.hasText()) {
            return false;
        }
        Long managerChatId = managerConfig.getChatId();
        if (managerChatId == null || managerChatId == 0L) {
            return false;
        }
        if (!managerChatId.equals(message.getChatId())) {
            return false;
        }
        if (managerConfig.getUserId() != null && managerConfig.getUserId() != 0L) {
            if (message.getFrom() == null || !managerConfig.getUserId().equals(message.getFrom().getId().longValue())) {
                return false;
            }
        }
        return message.getReplyToMessage() != null;
    }

    @Override
    @Transactional
    public SendMessage handle(Message message) {
        Long managerChatId = message.getChatId();
        Integer replyToMessageId = message.getReplyToMessage().getMessageId();

        Integer basePrice = parsePrice(message.getText());
        if (basePrice == null) {
            return SendMessage.builder()
                    .chatId(managerChatId)
                    .text("Не смог распознать цену. Ответь на сообщение с моделью числом, например: 65000")
                    .build();
        }

        PriceRequestEntity request = priceRequestRepository
                .findByManagerChatIdAndManagerRequestMessageIdAndStatus(managerChatId, replyToMessageId, PriceRequestStatus.OPEN)
                .orElse(null);

        if (request == null) {
            return SendMessage.builder()
                    .chatId(managerChatId)
                    .text("Не нашёл активную заявку по этому сообщению (возможно уже обработана).")
                    .build();
        }

        // Парсим информацию о телефоне из modelKey или modelDisplay (или из другого источника)
        PhoneInfoDto phoneInfo = parsePhoneInfo(request.getModelKey(), request.getModelDisplay());
        if (phoneInfo == null) {
            return SendMessage.builder()
                    .chatId(managerChatId)
                    .text("Не удалось определить параметры телефона (бренд, модель, память) из заявки.")
                    .build();
        }

        int minPrice = (int) Math.round(basePrice * priceDefaultsConfig.getMinMultiplier());
        int maxPrice = (int) Math.round(basePrice * priceDefaultsConfig.getMaxMultiplier());

        PhonePriceEntity entity = phonePriceRepository.findByBrandAndModelAndStorageGb(
                phoneInfo.getBrand(),
                phoneInfo.getModel(),
                phoneInfo.getStorageGb()
        ).orElseGet(PhonePriceEntity::new);
        entity.setBrand(phoneInfo.getBrand());
        entity.setModel(phoneInfo.getModel());
        entity.setStorageGb(phoneInfo.getStorageGb());
        entity.setBasePrice(basePrice);
        entity.setMinPrice(minPrice);
        entity.setMaxPrice(maxPrice);
        phonePriceRepository.save(entity);

        request.setStatus(PriceRequestStatus.RESOLVED);
        request.setResolvedAt(LocalDateTime.now());
        request.setResolvedPrice(basePrice);
        priceRequestRepository.save(request);

        List<Long> waiterChatIds = priceRequestWaiterRepository.findAllByRequest_Id(request.getId()).stream()
                .map(w -> w.getWaiterChatId())
                .distinct()
                .toList();

        String userText = "Цена для \"" + request.getModelDisplay() + "\": " + basePrice + " ₽";
        for (Long chatId : waiterChatIds) {
            telegramAsyncMessageSender.sendMessageAsync(
                    chatId.toString(),
                    () -> SendMessage.builder().chatId(chatId).text(userText).build(),
                    (t) -> SendMessage.builder().chatId(chatId).text(userText).build()
            );
        }

        return SendMessage.builder()
                .chatId(managerChatId)
                .text("Сохранил цену: \"" + request.getModelDisplay() + "\" = " + basePrice + " ₽. Отправил пользователям: " + waiterChatIds.size())
                .build();
    }

    private Integer parsePrice(String text) {
        if (text == null) {
            return null;
        }
        Matcher m = PRICE_PATTERN.matcher(text);
        if (!m.find()) {
            return null;
        }
        String digits = m.group(1).replaceAll("\\s+", "");
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            log.warn("Cannot parse manager price: {}", text, e);
            return null;
        }
    }

    private PhoneInfoDto parsePhoneInfo(String modelKey, String modelDisplay) {
        // Пример простого парсинга: Apple_iPhone13_128 или "Apple iPhone 13 128"
        String source = modelKey != null ? modelKey : modelDisplay;
        if (source == null) return null;
        String[] parts = source.replace('_', ' ').split("[ ,]+", 3);
        if (parts.length < 3) return null;
        return PhoneInfoDto.builder()
                .brand(parts[0])
                .model(parts[1])
                .storageGb(parseIntSafe(parts[2]))
                .build();
    }

    private Integer parseIntSafe(String s) {
        try {
            return Integer.valueOf(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
