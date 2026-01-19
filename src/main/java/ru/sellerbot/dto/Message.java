package ru.sellerbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(
        @JsonProperty("role") String role,
        @JsonProperty("content") Object content
) {}
