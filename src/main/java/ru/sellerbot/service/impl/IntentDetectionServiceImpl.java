package ru.sellerbot.service.impl;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import ru.sellerbot.dto.IntentDetectionResult;
import ru.sellerbot.model.enums.IntentType;
import ru.sellerbot.service.IntentDetectionService;

@Service
public class IntentDetectionServiceImpl implements IntentDetectionService {

    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d{4,6})");
    private static final Pattern IPHONE_PATTERN = Pattern.compile("iphone\\s*(\\d{2}|\\d)(?:\\s*(pro(?:\\s*max)?))?", Pattern.CASE_INSENSITIVE);
    private static final Pattern SAMSUNG_PATTERN = Pattern.compile("samsung\\s*galaxy\\s*([a-z]?\\d{1,2}\\s*(ultra|plus)?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern XIAOMI_PATTERN = Pattern.compile("(xiaomi|redmi|mi)\\s*([a-z]?\\d{1,2}\\s*(pro|note|max)?)", Pattern.CASE_INSENSITIVE);

    @Override
    public IntentDetectionResult detect(String text) {
        if (text == null || text.isBlank()) {
            return IntentDetectionResult.unknown();
        }

        String normalized = text.toLowerCase(Locale.ROOT);
        IntentType intent = resolveIntent(normalized);
        String model = extractModel(text);
        Integer price = extractPrice(normalized);

        return new IntentDetectionResult(intent, model, price);
    }

    private IntentType resolveIntent(String normalized) {
        boolean wantsBuy = containsAny(normalized, "купить", "ищу", "хочу купить", "покупка");
        boolean wantsSell = containsAny(normalized, "продать", "продаю", "хочу продать", "выкуп", "сдать");

        if (wantsBuy && !wantsSell) {
            return IntentType.BUY_PHONE;
        }
        if (wantsSell && !wantsBuy) {
            return IntentType.SELL_PHONE;
        }
        return IntentType.UNKNOWN;
    }

    private boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String extractModel(String text) {
        Matcher iphone = IPHONE_PATTERN.matcher(text);
        if (iphone.find()) {
            String model = "iPhone " + iphone.group(1);
            if (iphone.group(2) != null) {
                model += " " + iphone.group(2).trim();
            }
            return model.trim();
        }

        Matcher samsung = SAMSUNG_PATTERN.matcher(text);
        if (samsung.find()) {
            return ("Samsung Galaxy " + samsung.group(1)).trim();
        }

        Matcher xiaomi = XIAOMI_PATTERN.matcher(text);
        if (xiaomi.find()) {
            return (xiaomi.group(1) + " " + xiaomi.group(2)).trim();
        }

        return null;
    }

    private Integer extractPrice(String normalized) {
        Matcher matcher = PRICE_PATTERN.matcher(normalized.replace(" ", ""));
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
