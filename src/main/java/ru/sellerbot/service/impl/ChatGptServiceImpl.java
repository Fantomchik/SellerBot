package ru.sellerbot.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;
import ru.sellerbot.config.GptConfig;
import ru.sellerbot.dto.ChatCompletionRequest;
import ru.sellerbot.dto.GptDealResponse;
import ru.sellerbot.dto.Message;
import ru.sellerbot.openai.OpenAIClient;
import ru.sellerbot.service.ChatGptHistoryService;
import ru.sellerbot.service.ChatGptService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGptServiceImpl implements ChatGptService {
    private final OpenAIClient openAIClient;
    private final ChatGptHistoryService chatGptHistoryService;
    private final GptConfig gptConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.token}")
    private String openAiToken;

    @Value("${gpt.model}")
    private String gptModel;

    @Nonnull
    public String getResponseChatForUser(
            Long userId,
            String userTextInput
    ) {
        return getResponseChatForUserWithImages(userId, userTextInput, null,null);
    }

    @Nonnull
    public String getResponseChatForUserWithSingleImage(
            Long userId,
            String userTextInput,
            String base64Image
    ) {
        List<Map<String, Object>> images = null;
        if (base64Image != null) {
            images = List.of(
                    Map.of(
                            "type", "image_url",
                            "image_url", Map.of("url", "data:image/jpeg;base64," + base64Image)
                    )
            );
        }
        return getResponseChatForUserWithImages(userId, userTextInput, null, images);
    }

    @Nonnull
    public String getResponseChatForUserWithImages(
            Long userId,
            String userTextInput,
            String prompt,
            List<Map<String, Object>> newImages
    ) {
        log.info("Starting getResponseChatForUser for userId: {}, text: {}, images count: {}",
                userId, userTextInput, newImages != null ? newImages.size() : 0);

        chatGptHistoryService.createHistoryIfNotExist(userId);

        // Add system prompt if it's a new chat
        if (chatGptHistoryService.getUserHistory(userId).map(history -> history.chatMessages().isEmpty()).orElse(true)) {
            log.info("Adding system prompt for new chat");
            var systemMessage = new Message(
                    "system",
                    prompt
            );
            chatGptHistoryService.addMessageToHistory(userId, systemMessage);
        }

        Message userMessage;
        List<Map<String, Object>> allImages = new ArrayList<>();

        // Добавляем изображения из истории
        chatGptHistoryService.getUserHistory(userId).ifPresent(history -> {
            allImages.addAll(history.images());
        });

        // Добавляем новые изображения
        if (newImages != null) {
            allImages.addAll(newImages);
        }

        if (!allImages.isEmpty()) {
            log.info("Creating message with {} images for user {}", allImages.size(), userId);
            // Create a message with image content
            List<Object> content = new ArrayList<>();
            content.add(Map.of("type", "text", "text", userTextInput));

            // Добавляем изображения с указанием типа
            for (Map<String, Object> image : allImages) {
                @SuppressWarnings("unchecked")
                Map<String, String> imageUrl = (Map<String, String>) image.get("image_url");
                if (imageUrl != null && imageUrl.get("url") != null) {
                    content.add(Map.of(
                            "type", "image_url",
                            "image_url", Map.of("url", imageUrl.get("url"))
                    ));
                }
            }

            log.info("Message content structure: {}", content);

            userMessage = new Message(
                    "user",
                    content
            );
        } else {
            log.info("Creating text message for user {}", userId);
            userMessage = new Message(
                    "user",
                    userTextInput
            );
        }

        var history = chatGptHistoryService.addMessageToHistory(userId, userMessage);
        log.info("Current chat history size: {}", history.chatMessages().size());

        var request = new ChatCompletionRequest(
                gptConfig.getModel(),
                history.chatMessages(),
                4000,
                1.0,
                1.0
        );

        log.info("Sending request to GPT with model: {}", request.model());
        log.info("Request messages: {}", request.messages());

        var response = openAIClient.createChatCompletion(request);
        log.info("Received response from GPT: {}", response);

        var messageFromGpt = response.choices().get(0).message();
        log.info("GPT response content type: {}", messageFromGpt.content().getClass().getName());
        log.info("GPT response content: {}", messageFromGpt.content());

        // Convert the response to a text message to maintain chat history
        Message textMessage = new Message(
                "assistant",
                messageFromGpt.content().toString()
        );

        chatGptHistoryService.addMessageToHistory(userId, textMessage);

        return messageFromGpt.content().toString();
    }

    @Override
    public String getSingleAnswer(String systemPrompt, String userMessage) {
        var system = new Message("system", systemPrompt);
        var user = new Message("user", userMessage);

        var request = new ChatCompletionRequest(
                gptConfig.getModel(),
                List.of(system, user),
                1500,
                0.7,
                0.0
        );

        var response = openAIClient.createChatCompletion(request);
        return response.choices().get(0).message().content().toString();
    }

    public String askGpt(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openai.com/v1/chat/completions";
        String requestBody = String.format(
                "{ \"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}] }",
                gptModel, prompt.replace("\"", "\\\"")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiToken);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Ошибка при обработке ответа ИИ";
        }
    }

    public GptDealResponse getDealSystemResponseForUser(Long userId, String userTextInput) {
        return getDealSystemResponseForUserWithImages(userId, userTextInput, gptConfig.getSystemPrompt(), null);
    }

    public GptDealResponse getDealSystemResponseForUserWithImages(Long userId, String userTextInput, String prompt, List<Map<String, Object>> newImages) {
        log.info("Starting getDealSystemResponseForUser for userId: {}, text: {}, images count: {}",
                userId, userTextInput, newImages != null ? newImages.size() : 0);

        chatGptHistoryService.createHistoryIfNotExist(userId);

        if (chatGptHistoryService.getUserHistory(userId).map(history -> history.chatMessages().isEmpty()).orElse(true)) {
            log.info("Adding system prompt for new chat");
            var systemMessage = new Message(
                    "system",
                    prompt
            );
            chatGptHistoryService.addMessageToHistory(userId, systemMessage);
        }

        Message userMessage;
        List<Map<String, Object>> allImages = new ArrayList<>();
        chatGptHistoryService.getUserHistory(userId).ifPresent(history -> {
            allImages.addAll(history.images());
        });
        if (newImages != null) {
            allImages.addAll(newImages);
        }
        if (!allImages.isEmpty()) {
            log.info("Creating message with {} images for user {}", allImages.size(), userId);
            List<Object> content = new ArrayList<>();
            content.add(Map.of("type", "text", "text", userTextInput));
            for (Map<String, Object> image : allImages) {
                @SuppressWarnings("unchecked")
                Map<String, String> imageUrl = (Map<String, String>) image.get("image_url");
                if (imageUrl != null && imageUrl.get("url") != null) {
                    content.add(Map.of(
                            "type", "image_url",
                            "image_url", Map.of("url", imageUrl.get("url"))
                    ));
                }
            }
            log.info("Message content structure: {}", content);
            userMessage = new Message(
                    "user",
                    content
            );
        } else {
            log.info("Creating text message for user {}", userId);
            userMessage = new Message(
                    "user",
                    userTextInput
            );
        }
        var history = chatGptHistoryService.addMessageToHistory(userId, userMessage);
        log.info("Current chat history size: {}", history.chatMessages().size());
        var request = new ChatCompletionRequest(
                gptConfig.getModel(),
                history.chatMessages(),
                4000,
                1.0,
                1.0
        );
        log.info("Sending request to GPT with model: {}", request.model());
        log.info("Request messages: {}", request.messages());
        var response = openAIClient.createChatCompletion(request);
        log.info("Received response from GPT: {}", response);
        var messageFromGpt = response.choices().get(0).message();
        log.info("GPT response content type: {}", messageFromGpt.content().getClass().getName());
        log.info("GPT response content: {}", messageFromGpt.content());
        Message textMessage = new Message(
                "assistant",
                messageFromGpt.content().toString()
        );
        chatGptHistoryService.addMessageToHistory(userId, textMessage);
        // Парсим JSON-ответ в DTO
        try {
            return objectMapper.readValue(messageFromGpt.content().toString(), GptDealResponse.class);
        } catch (Exception e) {
            log.error("Ошибка парсинга JSON-ответа GPT в GptDealResponse", e);
            return null;
        }
    }
}
