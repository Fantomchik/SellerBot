package ru.sellerbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sellerbot.config.GptConfig;
import ru.sellerbot.dto.GptDealResponse;
import ru.sellerbot.dto.IntentDetectionResult;
import ru.sellerbot.dto.NegotiationResult;
import ru.sellerbot.model.enums.DialogState;
import ru.sellerbot.model.enums.IntentType;
import ru.sellerbot.model.PhonePrice;
import ru.sellerbot.model.UserSession;
import ru.sellerbot.service.ActionProcessingService;
import ru.sellerbot.service.ChatGptService;
import ru.sellerbot.service.ConversationService;
import ru.sellerbot.service.IntentDetectionService;
import ru.sellerbot.service.NegotiationService;
import ru.sellerbot.service.PhonePriceService;
import ru.sellerbot.service.PriceRequestService;
import ru.sellerbot.service.SessionService;
import ru.sellerbot.util.ModelKeyNormalizer;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final IntentDetectionService intentDetectionService;
    private final PhonePriceService phonePriceService;
    private final NegotiationService negotiationService;
    private final SessionService sessionService;
    private final ChatGptService chatGptService;
    private final GptConfig gptConfig;
    private final PriceRequestService priceRequestService;
    private final ActionProcessingService actionProcessingService;

    @Override
    public SendMessage handleUserMessage(Long chatId, Integer messageId, String text) {
        // Новый путь: если в конфиге есть deal-system-prompt, используем ИИ-логику
        if (gptConfig.getDealSystemPrompt() != null && !gptConfig.getDealSystemPrompt().isBlank()) {
            GptDealResponse gptResponse = ((ChatGptServiceImpl) chatGptService).getDealSystemResponseForUser(chatId, text);
            return actionProcessingService.processAction(chatId, gptResponse);
        }

        UserSession session = sessionService.getSession(chatId);

        // Если ждём цену от клиента
        if (session.getState() == DialogState.WAITING_PRICE) {
            var price = intentDetectionService.detect(text).offeredPrice();
            if (price == null) {
                return buildPlain(chatId, "Пожалуйста, укажите цену, за которую готовы продать телефон.");
            }
            session.setCurrentOffer(price);
            session.setState(DialogState.NEGOTIATION);
            sessionService.saveSession(session);
            return handleNegotiation(chatId, session, price);
        }

        IntentDetectionResult detection = intentDetectionService.detect(text);
        session.setIntent(detection.intent());

        if (detection.intent() == IntentType.UNKNOWN) {
            return buildPlain(chatId, "Не совсем понял, хотите купить или продать телефон? Напишите, например: \"Хочу продать iPhone 13 за 70 000\" или \"Хочу купить Samsung Galaxy S23\".");
        }

        // Обновляем модель, если удалось извлечь
        if (detection.phoneModel() != null) {
            session.setPhoneModel(detection.phoneModel());
        }

        if (session.getPhoneModel() == null) {
            session.setState(DialogState.WAITING_MODEL);
            sessionService.saveSession(session);
            return buildPlain(chatId, "Уточните, пожалуйста, модель телефона (например, iPhone 13 Pro или Samsung Galaxy S23).");
        }

        // --- Новый способ получения цены ---
        // Здесь предполагается, что session.getPhoneModel() возвращает строку вида "Apple 13 128" или аналогично.
        // В реальном проекте лучше хранить brand/model/storageGb отдельно в UserSession и IntentDetectionResult.
        String[] parts = session.getPhoneModel().split(" ");
        String brand = parts.length > 0 ? parts[0] : null;
        String model = parts.length > 1 ? parts[1] : null;
        Integer storageGb = parts.length > 2 ? Integer.valueOf(parts[2]) : null;
        PhonePrice price = phonePriceService.getPhonePrice(brand, model, storageGb).orElse(null);
        if (price == null) {
            priceRequestService.ensureOpenRequestAndSubscribeWaiter(
                    brand + " " + model + " " + (storageGb != null ? storageGb : ""),
                    session.getPhoneModel(),
                    chatId,
                    messageId
            );

            session.setState(DialogState.HANDOVER_TO_MANAGER);
            sessionService.saveSession(session);
            return buildPlain(chatId, "Этой модели пока нет в прайс-листе. Уточняю цену у менеджера и напишу вам, как только получу ответ.");
        }

        session.setPrice(price);
        sessionService.saveSession(session);

        if (detection.intent() == IntentType.BUY_PHONE) {
            session.setState(DialogState.OFFER_SENT);
            sessionService.saveSession(session);
            NegotiationResult result = negotiationService.proposeBuyOffer(price);
            return buildAiResponse(chatId, session, result, text);
        }

        // SELL_PHONE
        Integer offeredPrice = detection.offeredPrice();
        if (offeredPrice == null) {
            session.setState(DialogState.WAITING_PRICE);
            sessionService.saveSession(session);
            return buildPlain(chatId, "По какой цене вы готовы продать " + session.getPhoneModel() + "?");
        }

        session.setCurrentOffer(offeredPrice);
        session.setState(DialogState.NEGOTIATION);
        sessionService.saveSession(session);
        return handleNegotiation(chatId, session, offeredPrice);
    }

    private SendMessage handleNegotiation(Long chatId, UserSession session, int customerOffer) {
        NegotiationResult result = negotiationService.negotiateSellOffer(session.getPrice(), customerOffer);
        if (result.status().isTerminal()) {
            session.setState(DialogState.DONE);
        }
        sessionService.saveSession(session);
        return buildAiResponse(chatId, session, result, null);
    }

    private SendMessage buildAiResponse(Long chatId, UserSession session, NegotiationResult negotiationResult, String userText) {
        String systemPrompt = gptConfig.getDealSystemPrompt() != null
                ? gptConfig.getDealSystemPrompt()
                : "Ты вежливый ассистент магазина телефонов. Отвечай кратко, указывай цену и следующие шаги.";
        StringBuilder userMessage = new StringBuilder();
        userMessage.append("Диалог с клиентом о телефоне.\n");
        userMessage.append("Намерение: ").append(session.getIntent()).append("\n");
        userMessage.append("Модель: ").append(session.getPhoneModel()).append("\n");
        if (session.getPrice() != null) {
            userMessage.append("Прайс: base=").append(session.getPrice().getBasePrice())
                    .append(", min=").append(session.getPrice().getMinPrice())
                    .append(", max=").append(session.getPrice().getMaxPrice()).append("\n");
        }
        if (session.getCurrentOffer() != null) {
            userMessage.append("Предложение клиента: ").append(session.getCurrentOffer()).append("\n");
        }
        if (negotiationResult.counterOffer() != null) {
            userMessage.append("Наше встречное предложение: ").append(negotiationResult.counterOffer()).append("\n");
        }
        if (userText != null) {
            userMessage.append("Сообщение клиента: ").append(userText).append("\n");
        }
        String finalAnswer = chatGptService.getSingleAnswer(systemPrompt, userMessage.toString());
        return SendMessage.builder()
                .chatId(chatId)
                .text(finalAnswer)
                .build();
    }

    private SendMessage buildPlain(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
