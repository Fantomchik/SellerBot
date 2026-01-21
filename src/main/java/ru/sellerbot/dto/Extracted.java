package ru.sellerbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Extracted {
    private String model;
    private Integer basePrice;
    private Integer minPrice;
    private Integer maxPrice;
}
