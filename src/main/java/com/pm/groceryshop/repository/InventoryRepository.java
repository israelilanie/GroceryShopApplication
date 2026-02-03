package com.pm.groceryshop.repository;

import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByGroceryItem(GroceryItem groceryItem);
}
