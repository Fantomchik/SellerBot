package ru.sellerbot.service;

import java.util.Optional;
import ru.sellerbot.model.PhonePrice;

public interface PhonePriceService {
    Optional<PhonePrice> getPhonePrice(String brand, String model, Integer storageGb);
}
