package com.pm.groceryshop.repo;

import com.pm.groceryshop.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByShoppingCartIdAndGroceryItemId(Long cardId,Long itemId);
}
