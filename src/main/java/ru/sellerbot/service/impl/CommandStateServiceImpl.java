package ru.sellerbot.service.impl;

import org.springframework.stereotype.Service;
import ru.sellerbot.service.CommandStateService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommandStateServiceImpl implements CommandStateService {

    private final Map<Long, Boolean> startCommandStates = new ConcurrentHashMap<>();

    public void setStartCommandState(Long chatId, boolean state) {
        startCommandStates.put(chatId, state);
    }

    public boolean isStartCommandActive(Long chatId) {
        return startCommandStates.getOrDefault(chatId, false);
    }

    public void clearStartCommandState(Long chatId) {
        startCommandStates.remove(chatId);
    }
}
