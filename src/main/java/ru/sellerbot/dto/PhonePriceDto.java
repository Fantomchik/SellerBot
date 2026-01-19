package ru.sellerbot.dto;


public record PhonePriceDto(int marketPrice,
                            int minBuyPrice,
                            int maxBuyPrice) {}
