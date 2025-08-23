package org.assistantAPI.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name="seller")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seller {
    @Id private UUID id;
    @Column(nullable=false) private String name;

    @PrePersist void pre() { if (id==null) id = UUID.randomUUID(); }
}
