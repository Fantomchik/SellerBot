package ru.sellerbot.telegram.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sellerbot.service.ConversationService;
import ru.sellerbot.service.ManagerPriceReplyService;

@Service
@RequiredArgsConstructor
public class TelegramTextHandler {

    private final ConversationService conversationService;
    private final ManagerPriceReplyService managerPriceReplyService;

    public SendMessage processTextMessage(Message message) {
        if (managerPriceReplyService.canHandle(message)) {
            return managerPriceReplyService.handle(message);
        }
        var chatId = message.getChatId();
        var messageId = message.getMessageId();
        var text = message.getText();
        return conversationService.handleUserMessage(chatId, messageId, text);
    }
}
