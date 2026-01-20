package ru.sellerbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface ManagerPriceReplyService {
    boolean canHandle(Message message);

    /**
     * Handles manager reply (reply-to bot message), saves price to DB and notifies waiting users.
     */
    SendMessage handle(Message message);
}

