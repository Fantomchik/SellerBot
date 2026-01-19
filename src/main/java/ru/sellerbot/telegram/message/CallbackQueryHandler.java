package ru.sellerbot.telegram.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@RequiredArgsConstructor
public class CallbackQueryHandler {

    public SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        return SendMessage.builder()
                .chatId(chatId)
                .text("Кнопки пока не поддерживаются в этой версии. Напишите ваш запрос текстом.")
                .build();
    }

    public AnswerCallbackQuery answerCallbackQuery(CallbackQuery callbackQuery) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .build();
    }
}
