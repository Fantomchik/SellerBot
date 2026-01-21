package ru.sellerbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sellerbot.model.PhonePrice;
import ru.sellerbot.persistence.repository.PhonePriceRepository;
import ru.sellerbot.service.ManagerReplyProcessingService;

@Service
@RequiredArgsConstructor
public class ManagerReplyProcessingServiceImpl implements ManagerReplyProcessingService {
    private final PhonePriceRepository phonePriceRepository;

    @Override
    public SendMessage processManagerReply(Message message) {
        // Ожидается формат: brand model storage base min max
        String[] parts = message.getText().split("[ ,;]+", 6);
        if (parts.length < 6) {
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text("Ошибка: укажите все параметры через пробел: бренд модель память(ГБ) basePrice minPrice maxPrice")
                    .build();
        }
        try {
            String brand = parts[0];
            String model = parts[1];
            Integer storage = Integer.valueOf(parts[2]);
            Integer base = Integer.valueOf(parts[3]);
            Integer min = Integer.valueOf(parts[4]);
            Integer max = Integer.valueOf(parts[5]);
            var entity = new ru.sellerbot.persistence.entity.PhonePriceEntity();
            entity.setBrand(brand);
            entity.setModel(model);
            entity.setStorageGb(storage);
            entity.setBasePrice(base);
            entity.setMinPrice(min);
            entity.setMaxPrice(max);
            phonePriceRepository.save(entity);
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text("Данные успешно сохранены в базу!")
                    .build();
        } catch (Exception e) {
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text("Ошибка парсинга данных: " + e.getMessage())
                    .build();
        }
    }
}
