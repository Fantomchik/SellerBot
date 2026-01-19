package ru.sellerbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ConversationService {
    SendMessage handleUserMessage(Long chatId, String text);
}
