package org.assistantAPI.repository;

import org.assistantAPI.domain.*;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {
    Optional<Product> findBySellerIdAndSku(Long sellerId, String sku);
    List<Product> findBySellerIdOrderByUpdatedAtDesc(Long sellerId);
    Optional<Product> findByIdAndSellerId(UUID id, Long sellerId);
    @Query("""
    select p from Product p
    where (:model is null or lower(p.model) like lower(concat('%', :model, '%')))
      and (:storage is null or p.memory = :storage)
      and (:minPrice is null or p.price >= :minPrice)
      and (:maxPrice is null or p.price <= :maxPrice)
  """)
    List<Product> search(
            @Param("model") String model,
            @Param("storage") Integer storage,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice);
}
