package org.assistantAPI.mapper;

import lombok.experimental.UtilityClass;
import org.assistantAPI.domain.Product;
import org.assistantAPI.dto.ProductDto;
import org.assistantAPI.dto.UpdateProductRequest;

@UtilityClass
public class ProductMapper {

    public static ProductDto toDto(Product p) {
        return new ProductDto(p.getId(), p.getSku(),
                p.getPrice(), p.getCurrency(), p.getQuantity(), p.getUpdatedAt(), p.getAttributes()
        );
    }
    public static void apply(Product p, UpdateProductRequest r) {
        if (r.price()!=null) p.setPrice(r.price());
        if (r.currency()!=null) p.setCurrency(r.currency());
        if (r.quantity()!=null) p.setQuantity(r.quantity());
    }
}
