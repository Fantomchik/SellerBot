package ru.sellerbot.persistence.mapper;

import lombok.experimental.UtilityClass;
import ru.sellerbot.model.PhonePrice;
import ru.sellerbot.persistence.entity.PhonePriceEntity;

@UtilityClass
public class PhonePriceMapper {

    public PhonePrice toDomain(PhonePriceEntity entity) {
        if (entity == null) {
            return null;
        }
        return PhonePrice.builder()
                .model(entity.getModel())
                .basePrice(entity.getBasePrice())
                .minPrice(entity.getMinPrice())
                .maxPrice(entity.getMaxPrice())
                .build();
    }
}
