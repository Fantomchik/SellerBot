package ru.sellerbot.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sellerbot.dto.CreateTranscriptionRequest;
import ru.sellerbot.openai.OpenAIClient;
import ru.sellerbot.service.TranscribeVoiceToTextService;

import java.io.File;

@Service
@AllArgsConstructor
public class TranscribeVoiceToTextServiceImpl implements TranscribeVoiceToTextService {
    private final OpenAIClient openAIClient;

    public String transcribe(File audioFile) {
        var request = new CreateTranscriptionRequest(audioFile, "whisper-1");
        var response = openAIClient.createTranscription(request);
        return response.text();
    }
}
