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
  where (:minPrice is null or p.price >= :minPrice)
    and (:maxPrice is null or p.price <= :maxPrice)
  order by p.updatedAt desc
""")
    List<Product> search(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

}
