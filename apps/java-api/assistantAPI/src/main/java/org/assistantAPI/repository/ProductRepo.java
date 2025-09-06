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

    @Query(value = """
  select * from product p
  where
    (:q is null
      or lower(p.attributes->>'title') like lower(concat('%', :q, '%'))
      or lower(p.attributes->>'description') like lower(concat('%', :q, '%'))
      or exists (
           select 1 from jsonb_each_text(p.attributes) kv
           where lower(kv.value) like lower(concat('%', :q, '%'))
      )
    )
    and (:minPrice is null or p.price >= :minPrice)
    and (:maxPrice is null or p.price <= :maxPrice)
    and (:currency is null or p.currency = :currency)
    and (:inStockOnly = false or coalesce(p.quantity,0) > 0)
    and (:attrsJson is null or p.attributes @> cast(:attrsJson as jsonb))
    and (:minStorage is null or (p.attributes->>'storage')::int >= :minStorage)
    and (:color is null or lower(p.attributes->>'color') = lower(:color))
  order by p.updated_at desc
  limit :limit offset :offset
  """, nativeQuery = true)
    List<Product> search(
            @Param("q") String q,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("currency") String currency,
            @Param("inStockOnly") boolean inStockOnly,
            @Param("attrsJson") String attrsJson,
            @Param("minStorage") Integer minStorage,
            @Param("color") String color,
            @Param("limit") int limit,
            @Param("offset") int offset
    );


}
