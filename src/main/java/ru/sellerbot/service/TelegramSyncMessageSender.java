package ru.sellerbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TelegramSyncMessageSender {
    Message send(SendMessage message);
}

