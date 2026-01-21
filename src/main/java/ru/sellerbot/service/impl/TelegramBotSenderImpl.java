package ru.sellerbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sellerbot.service.TelegramBotSender;
import ru.sellerbot.telegram.TelegramBot;

@Service
@RequiredArgsConstructor
public class TelegramBotSenderImpl implements TelegramBotSender {
    private final ApplicationContext applicationContext;

    @Override
    public void send(SendMessage message) {
        try {
            TelegramBot telegramBot = applicationContext.getBean(TelegramBot.class);
            telegramBot.execute(message);
        } catch (Exception e) {
            // логирование ошибки
        }
    }
}
