package com.app.ecommerce.cart.repository;

import com.app.ecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.cartStatus = 'ACTIVE'")
    Optional<Cart> findByUserIdAndCartStatusActive(UUID userId);
}
