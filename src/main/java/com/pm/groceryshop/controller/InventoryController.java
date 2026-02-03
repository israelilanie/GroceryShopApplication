package com.pm.groceryshop.controller;

import com.pm.groceryshop.dto.InventoryAdjustmentRequest;
import com.pm.groceryshop.model.InventoryItem;
import com.pm.groceryshop.service.GroceryShopService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final GroceryShopService groceryShopService;

    public InventoryController(GroceryShopService groceryShopService) {
        this.groceryShopService = groceryShopService;
    }

    @GetMapping
    public List<InventoryItem> listInventory() {
        return groceryShopService.listInventory();
    }

    @PostMapping("/adjust")
    public InventoryItem adjustInventory(@Valid @RequestBody InventoryAdjustmentRequest request) {
        return groceryShopService.adjustInventory(request);
    }
}
