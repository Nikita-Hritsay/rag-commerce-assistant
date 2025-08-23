package org.assistantAPI.repository;

import org.assistantAPI.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    Optional<Product> findBySellerIdAndSku(UUID sellerId, String sku);
    List<Product> findBySellerId(UUID sellerId);
}
