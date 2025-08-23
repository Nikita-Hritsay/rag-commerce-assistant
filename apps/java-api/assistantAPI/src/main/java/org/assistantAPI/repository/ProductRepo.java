package org.assistantAPI.repository;

import org.assistantAPI.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {
    Optional<Product> findBySellerIdAndSku(UUID sellerId, String sku);
}
