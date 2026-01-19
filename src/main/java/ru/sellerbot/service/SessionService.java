package ru.sellerbot.service;

import ru.sellerbot.model.UserSession;

public interface SessionService {
    UserSession getSession(Long chatId);

    void saveSession(UserSession session);

    void resetSession(Long chatId);
}
