package ru.sellerbot.service;

import ru.sellerbot.dto.ChatHistory;
import ru.sellerbot.dto.Message;

import java.util.Map;
import java.util.Optional;

public interface ChatGptHistoryService {
    Optional<ChatHistory> getUserHistory(Long userId);
    void createHistory(Long userId);
    void clearHistory(Long userId);
    ChatHistory addMessageToHistory(Long userId, Message message);
    ChatHistory addImageToHistory(Long userId, Map<String, Object> image);
    void createHistoryIfNotExist(Long userId);
    void clearImages(Long userId);
}
