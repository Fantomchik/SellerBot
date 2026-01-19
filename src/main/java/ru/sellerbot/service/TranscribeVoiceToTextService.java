package ru.sellerbot.service;

import java.io.File;

public interface TranscribeVoiceToTextService {
    String transcribe(File audioFile);
}
