package ru.sellerbot.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PhonePrice {
    String model;
    Integer basePrice;   // средняя рыночная
    Integer minPrice;    // нижняя граница (выкуп)
    Integer maxPrice;    // верхняя граница (продажа)
}
