package ru.sellerbot.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sellerbot.model.PhonePrice;
import ru.sellerbot.persistence.mapper.PhonePriceMapper;
import ru.sellerbot.persistence.repository.PhonePriceRepository;
import ru.sellerbot.service.PhonePriceService;

@Service
@RequiredArgsConstructor
public class PhonePriceServiceImpl implements PhonePriceService {

    private final PhonePriceRepository phonePriceRepository;

    @Override
    public Optional<PhonePrice> getPhonePrice(String phoneModel) {
        if (phoneModel == null || phoneModel.isBlank()) {
            return Optional.empty();
        }
        return phonePriceRepository.findByModelIgnoreCase(phoneModel.trim())
                .map(PhonePriceMapper::toDomain);
    }
}
