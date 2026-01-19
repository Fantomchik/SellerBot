package ru.sellerbot.service.impl;

import org.springframework.stereotype.Service;
import ru.sellerbot.dto.NegotiationResult;
import ru.sellerbot.model.PhonePrice;
import ru.sellerbot.service.NegotiationService;

@Service
public class NegotiationServiceImpl implements NegotiationService {

    @Override
    public NegotiationResult proposeBuyOffer(PhonePrice price) {
        // Для покупки клиентом: предлагаем верхнюю границу или базовую цену
        int base = safe(price.getBasePrice());
        int max = Math.max(base, safe(price.getMaxPrice()));

        return NegotiationResult.counter(max, "Исходное предложение для покупки телефона клиентом.");
    }

    @Override
    public NegotiationResult negotiateSellOffer(PhonePrice price, int customerOffer) {
        int base = safe(price.getBasePrice());
        int min = safe(price.getMinPrice());

        // Если клиент назвал цену выше либо равной базовой — принимаем
        if (customerOffer >= base) {
            return NegotiationResult.accept(customerOffer);
        }

        // Если клиентская цена в допустимом диапазоне — предлагаем встречную ближе к базе
        if (customerOffer >= min) {
            int counter = Math.max(min, (customerOffer + base) / 2);
            return NegotiationResult.counter(counter, "Встречное предложение в рамках допустимого диапазона.");
        }

        // Цена ниже минимума — предлагаем минимально возможную
        return NegotiationResult.counter(min, "Минимальная цена выкупа для данной модели.");
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}
