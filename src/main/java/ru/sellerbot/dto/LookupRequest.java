package ru.sellerbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LookupRequest {
    private String brand;
    private String model;
    private Integer storage_gb;
    private String notes;
}
