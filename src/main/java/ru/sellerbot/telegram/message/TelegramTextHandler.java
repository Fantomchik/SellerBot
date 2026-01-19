package ru.sellerbot.telegram.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sellerbot.service.ConversationService;

@Service
@RequiredArgsConstructor
public class TelegramTextHandler {

    private final ConversationService conversationService;

    public SendMessage processTextMessage(Message message) {
        var chatId = message.getChatId();
        var text = message.getText();
        return conversationService.handleUserMessage(chatId, text);
    }
}
