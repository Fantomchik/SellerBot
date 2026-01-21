package ru.sellerbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sellerbot.dto.GptDealResponse;
import ru.sellerbot.service.ActionProcessingService;
import ru.sellerbot.service.TelegramManagerNotifierService;
import ru.sellerbot.model.enums.Action;

@Service
@RequiredArgsConstructor
public class ActionProcessingServiceImpl implements ActionProcessingService {
    private final TelegramManagerNotifierService managerNotifierService;
    @Value("${telegram.manager.chat-id}")
    private Long managerChatId;
    @Override
    public SendMessage processAction(Long chatId, GptDealResponse gptResponse) {
        if (gptResponse == null || gptResponse.getAction() == null) {
            return null;
        }
        switch (gptResponse.getAction()) {
            case SEND_TO_USER:
                return SendMessage.builder()
                        .chatId(chatId)
                        .text(gptResponse.getMessageToUser() != null ? gptResponse.getMessageToUser() : "Нет сообщения для пользователя.")
                        .build();
            case ESCALATE_TO_HUMAN:
                // Отправляем менеджеру запрос на уточнение
                managerNotifierService.notifyManager(managerChatId, gptResponse);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Ваш запрос передан менеджеру. Ожидайте ответа.")
                        .build();
            case ASK_USER:
            case LOOKUP_BUYBACK_PRICE:
            case LOOKUP_BUY_PRICE:
            default:
                // Для всех остальных action ответ возвращается обратно в GPT для дальнейшей обработки
                return null;
        }
    }
}
