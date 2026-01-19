package ru.sellerbot.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import ru.sellerbot.model.DialogState;
import ru.sellerbot.model.UserSession;
import ru.sellerbot.service.SessionService;

@Service
public class SessionServiceImpl implements SessionService {

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    @Override
    public UserSession getSession(Long chatId) {
        return sessions.computeIfAbsent(chatId, id -> UserSession.builder()
                .chatId(id)
                .state(DialogState.WAITING_MODEL)
                .build());
    }

    @Override
    public void saveSession(UserSession session) {
        sessions.put(session.getChatId(), session);
    }

    @Override
    public void resetSession(Long chatId) {
        sessions.remove(chatId);
    }
}
