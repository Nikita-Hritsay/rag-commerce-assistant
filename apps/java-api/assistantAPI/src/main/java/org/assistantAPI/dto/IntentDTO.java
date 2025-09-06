package org.assistantAPI.dto;

public record IntentDTO(
        String q,
        Double minPrice,
        Double maxPrice,
        String currency,
        boolean inStockOnly,
        String attrsJson,
        Integer minStorage,
        String color,
        int limit,
        int offset) {
}
