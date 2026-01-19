package ru.sellerbot.service;

import ru.sellerbot.dto.IntentDetectionResult;

public interface IntentDetectionService {
    IntentDetectionResult detect(String text);
}
