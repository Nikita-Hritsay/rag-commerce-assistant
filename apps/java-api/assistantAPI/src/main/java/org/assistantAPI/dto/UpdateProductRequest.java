package org.assistantAPI.dto;

public record UpdateProductRequest(
        String title, String description, Double price,
        String currency, Integer quantity
) {}