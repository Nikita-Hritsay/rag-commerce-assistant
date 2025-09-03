package org.assistantAPI.dto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record ProductDto(
        UUID id, String sku,
        Double price, String currency, Integer quantity,
        OffsetDateTime updatedAt, Map<String, Object> attributes
) {}