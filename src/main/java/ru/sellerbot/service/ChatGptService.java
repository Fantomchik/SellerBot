package ru.sellerbot.service;

import java.util.List;
import java.util.Map;

public interface ChatGptService {
    String getResponseChatForUser(
            Long userId,
            String userTextInput
    );
    String getResponseChatForUserWithSingleImage(
            Long userId,
            String userTextInput,
            String base64Image
    );
    String getResponseChatForUserWithImages(
            Long userId,
            String userTextInput,
            String prompt,
            List<Map<String, Object>> newImages
    );

    String getSingleAnswer(String systemPrompt, String userMessage);

    String askGpt(String prompt);
}
