package org.assistantAPI.mapper;

import org.assistantAPI.domain.Product;
import org.assistantAPI.dto.ProductDto;
import org.assistantAPI.dto.UpdateProductRequest;

public class ProductMapper {
    private ProductMapper(){}

    public static ProductDto toDto(Product p) {
        return new ProductDto(
                p.getId(), p.getSku(), p.getTitle(), p.getDescription(),
                p.getPrice(), p.getCurrency(), p.getQuantity(), p.getUpdatedAt()
        );
    }
    public static void apply(Product p, UpdateProductRequest r) {
        if (r.title()!=null) p.setTitle(r.title());
        if (r.description()!=null) p.setDescription(r.description());
        if (r.price()!=null) p.setPrice(r.price());
        if (r.currency()!=null) p.setCurrency(r.currency());
        if (r.quantity()!=null) p.setQuantity(r.quantity());
    }
}
