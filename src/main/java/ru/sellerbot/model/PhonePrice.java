package ru.sellerbot.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PhonePrice {
    String brand;      // производитель (iphone, samsung и т.д.)
    String model;      // модель (13, 15, galaxy s23 и т.д.)
    Integer storageGb; // объём памяти в ГБ
    Integer basePrice; // средняя рыночная
    Integer minPrice;  // нижняя граница (выкуп)
    Integer maxPrice;  // верхняя граница (продажа)
}
