package ru.sellerbot.service.impl;

import org.springframework.stereotype.Service;
import ru.sellerbot.service.UserStateService;
import ru.sellerbot.dto.UserData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStateServiceImpl implements UserStateService {

    private final Map<Long, UserData> userStates = new ConcurrentHashMap<>();

    public UserData getUserData(Long chatId) {
        return userStates.computeIfAbsent(chatId, k -> UserData.initial());
    }

    public void updateUserData(Long chatId, UserData userData) {
        userStates.put(chatId, userData);
    }

    public void resetUserData(Long chatId) {
        userStates.put(chatId, UserData.initial());
    }
}
