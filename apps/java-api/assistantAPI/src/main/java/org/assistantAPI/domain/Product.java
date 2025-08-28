package org.assistantAPI.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name="product",
        uniqueConstraints=@UniqueConstraint(name="ux_product_seller_sku", columnNames={"seller_id","sku"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id private UUID id;

    @Column(name="seller_id", nullable=false)
    private Long sellerId;

    @Column(nullable=false) private String sku;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    private Double price;
    private String currency;
    @Column(name="updated_at") private OffsetDateTime updatedAt;

    @PrePersist void pre() {
        if (id==null) id = UUID.randomUUID();
        if (currency==null) currency = "UAH";
        if (updatedAt==null) updatedAt = OffsetDateTime.now();
    }
}
