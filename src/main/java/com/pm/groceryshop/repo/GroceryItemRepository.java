package com.pm.groceryshop.repo;

import com.pm.groceryshop.model.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItem,Long> {

    boolean existsByName(String name);
    Optional<GroceryItem> findGroceryItemByName(String name);
}
