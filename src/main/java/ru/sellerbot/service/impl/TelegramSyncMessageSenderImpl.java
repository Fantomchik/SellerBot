package ru.sellerbot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sellerbot.service.TelegramSyncMessageSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramSyncMessageSenderImpl implements TelegramSyncMessageSender {

    private final @Lazy DefaultAbsSender defaultAbsSender;

    @Override
    public Message send(SendMessage message) {
        try {
            return defaultAbsSender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while sending sync message", e);
            throw new IllegalStateException("Telegram send failed", e);
        }
    }
}

