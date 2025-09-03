package org.assistantAPI.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity @Table(name="product",
        uniqueConstraints=@UniqueConstraint(name="ux_product_seller_sku", columnNames={"seller_id","sku"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id private UUID id;

    @Column(name="seller_id", nullable=false)
    private Long sellerId;

    @Column(nullable=false) private String sku;
    private Double price;
    private String currency;
    @Column(name="updated_at") private OffsetDateTime updatedAt;

    private Integer quantity;

    @PrePersist void pre() {
        if (id==null) id = UUID.randomUUID();
        if (currency==null) currency = "UAH";
        if (updatedAt==null) updatedAt = OffsetDateTime.now();
    }

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sellerId=" + sellerId +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", quantity=" + quantity +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
