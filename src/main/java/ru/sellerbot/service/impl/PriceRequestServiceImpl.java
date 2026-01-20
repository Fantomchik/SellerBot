package ru.sellerbot.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sellerbot.config.ManagerConfig;
import ru.sellerbot.persistence.entity.PriceRequestEntity;
import ru.sellerbot.persistence.entity.PriceRequestStatus;
import ru.sellerbot.persistence.entity.PriceRequestWaiterEntity;
import ru.sellerbot.persistence.repository.PriceRequestRepository;
import ru.sellerbot.persistence.repository.PriceRequestWaiterRepository;
import ru.sellerbot.service.PriceRequestService;
import ru.sellerbot.service.TelegramSyncMessageSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceRequestServiceImpl implements PriceRequestService {

    private final PriceRequestRepository priceRequestRepository;
    private final PriceRequestWaiterRepository priceRequestWaiterRepository;
    private final TelegramSyncMessageSender telegramSyncMessageSender;
    private final ManagerConfig managerConfig;

    @Override
    @Transactional
    public void ensureOpenRequestAndSubscribeWaiter(
            String modelKey,
            String modelDisplay,
            Long waiterChatId,
            Integer waiterMessageId
    ) {
        if (modelKey == null || modelKey.isBlank()) {
            return;
        }
        Long managerChatId = managerConfig.getChatId();
        if (managerChatId == null || managerChatId == 0L) {
            log.warn("Manager chat id is not configured; cannot create price request for modelKey={}", modelKey);
            return;
        }

        PriceRequestEntity request = priceRequestRepository
                .findByModelKeyAndStatusForUpdate(modelKey, PriceRequestStatus.OPEN)
                .orElseGet(() -> {
                    PriceRequestEntity r = new PriceRequestEntity();
                    r.setModelKey(modelKey);
                    r.setModelDisplay(modelDisplay != null && !modelDisplay.isBlank() ? modelDisplay.trim() : modelKey);
                    r.setStatus(PriceRequestStatus.OPEN);
                    r.setManagerChatId(managerChatId);
                    r.setCreatedAt(LocalDateTime.now());
                    return priceRequestRepository.save(r);
                });

        if (!priceRequestWaiterRepository.existsByRequest_IdAndWaiterChatId(request.getId(), waiterChatId)) {
            PriceRequestWaiterEntity waiter = new PriceRequestWaiterEntity();
            waiter.setRequest(request);
            waiter.setWaiterChatId(waiterChatId);
            waiter.setWaiterMessageId(waiterMessageId);
            waiter.setCreatedAt(LocalDateTime.now());
            priceRequestWaiterRepository.save(waiter);
        }

        if (request.getManagerRequestMessageId() == null) {
            String text = "Нужна цена для модели: " + request.getModelDisplay() + "\n"
                    + "Ответь на это сообщение числом (например: 65000).";

            var sent = telegramSyncMessageSender.send(SendMessage.builder()
                    .chatId(managerChatId)
                    .text(text)
                    .build());

            request.setManagerRequestMessageId(sent.getMessageId());
            priceRequestRepository.save(request);
        }
    }
}

