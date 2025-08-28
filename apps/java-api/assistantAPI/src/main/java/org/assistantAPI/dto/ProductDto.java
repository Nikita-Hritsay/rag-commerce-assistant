package org.assistantAPI.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductDto(
        UUID id, String sku, String title, String description,
        Double price, String currency, Integer quantity,
        OffsetDateTime updatedAt
) {}