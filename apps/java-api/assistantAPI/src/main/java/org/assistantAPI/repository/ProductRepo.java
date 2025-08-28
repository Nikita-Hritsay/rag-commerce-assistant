package org.assistantAPI.repository;

import org.assistantAPI.domain.*;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {
    Optional<Product> findBySellerIdAndSku(Long sellerId, String sku);
    List<Product> findBySellerIdOrderByUpdatedAtDesc(Long sellerId);
    Optional<Product> findByIdAndSellerId(UUID id, Long sellerId);
}
