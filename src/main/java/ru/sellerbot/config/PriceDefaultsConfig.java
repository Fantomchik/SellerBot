package ru.sellerbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "price.defaults")
public class PriceDefaultsConfig {
    /**
     * minPrice = round(basePrice * minMultiplier)
     */
    private double minMultiplier = 0.90;

    /**
     * maxPrice = round(basePrice * maxMultiplier)
     */
    private double maxMultiplier = 1.10;
}

