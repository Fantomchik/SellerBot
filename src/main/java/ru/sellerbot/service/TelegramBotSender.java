package ru.sellerbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface TelegramBotSender {
    void send(SendMessage message);
}
