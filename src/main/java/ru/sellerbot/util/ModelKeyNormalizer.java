package ru.sellerbot.util;

import java.util.Locale;

public final class ModelKeyNormalizer {
    private ModelKeyNormalizer() {
    }

    public static String normalize(String model) {
        if (model == null) {
            return null;
        }
        String trimmed = model.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        // Collapse whitespace and normalize case
        String collapsed = trimmed.replaceAll("\\s+", " ");
        return collapsed.toLowerCase(Locale.ROOT);
    }
}

