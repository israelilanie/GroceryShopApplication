package com.pm.groceryshop.repo;

import com.pm.groceryshop.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {
    Optional<ShoppingCart> findBySessionId(String sessionId);


    @Query("SELECT c FROM ShoppingCart c LEFT JOIN FETCH c.cartItems WHERE c.id = :id")
    Optional<ShoppingCart> findByIdWithItems(@Param("id") Long id);
}
