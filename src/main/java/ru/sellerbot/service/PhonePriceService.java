package ru.sellerbot.service;

import ru.sellerbot.model.PhonePrice;

public interface PhonePriceService {
    PhonePrice getPhonePrice(String phoneModel);
}
