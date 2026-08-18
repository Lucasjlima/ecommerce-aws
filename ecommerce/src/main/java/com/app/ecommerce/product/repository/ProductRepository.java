package com.app.ecommerce.product.repository;

import com.app.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE  p.id = :productId AND p.stockQuantity >= :quantity")
    int debitStock(@Param("productId") UUID productId, @Param("quantity") Long quantity);

    List<Product> findByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(UUID id);
}
