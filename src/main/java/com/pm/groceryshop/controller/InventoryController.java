package com.pm.groceryshop.controller;

import com.pm.groceryshop.dto.InventoryDTO;
import com.pm.groceryshop.model.Inventory;
import com.pm.groceryshop.service.GroceryItemService;
import com.pm.groceryshop.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final GroceryItemService groceryItemService;


    public InventoryController(InventoryService inventoryService, GroceryItemService groceryItemService) {
        this.inventoryService = inventoryService;
        this.groceryItemService = groceryItemService;
    }

    @GetMapping
    public List<InventoryDTO> getAllInventories(){
        return inventoryService.getAllInventories();
    }

    @GetMapping("/{id}")
    public InventoryDTO getInventoryByItemId(@PathVariable Long id){
        return inventoryService.getInventoryByItemId(id);
    }
    @PostMapping("/restock/{id}")
    public InventoryDTO restock(@PathVariable Long id, @RequestParam Integer stock){
        return inventoryService.increaseStock(id,stock);
    }

    @GetMapping("/low-stock")
    public List<InventoryDTO> getLowStockItem(@RequestParam Integer stock){
        return inventoryService.getLowStockItem(stock);
    }

    @PutMapping("/{id}")
    public InventoryDTO updateStockQuantityByItemId(@PathVariable Long id, @RequestParam Integer stock){
        return inventoryService.updateStockQuantityByItemId(id,stock);
    }
}
