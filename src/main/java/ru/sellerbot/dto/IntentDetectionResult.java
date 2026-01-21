package ru.sellerbot.dto;

import ru.sellerbot.model.enums.IntentType;

public record IntentDetectionResult(
        IntentType intent,
        String phoneModel,
        Integer offeredPrice
) {
    public static IntentDetectionResult unknown() {
        return new IntentDetectionResult(IntentType.UNKNOWN, null, null);
    }
}
