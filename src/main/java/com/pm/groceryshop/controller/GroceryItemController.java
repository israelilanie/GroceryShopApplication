package com.pm.groceryshop.controller;

import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.service.GroceryShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class GroceryItemController {

    private final GroceryShopService groceryShopService;

    public GroceryItemController(GroceryShopService groceryShopService) {
        this.groceryShopService = groceryShopService;
    }

    @GetMapping
    public List<GroceryItem> listItems() {
        return groceryShopService.listItems();
    }

    @GetMapping("/{id}")
    public GroceryItem getItem(@PathVariable Long id) {
        return groceryShopService.getItem(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroceryItem createItem(@Valid @RequestBody GroceryItem item) {
        return groceryShopService.createItem(item);
    }

    @PutMapping("/{id}")
    public GroceryItem updateItem(@PathVariable Long id, @Valid @RequestBody GroceryItem item) {
        return groceryShopService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        groceryShopService.deleteItem(id);
    }
}
