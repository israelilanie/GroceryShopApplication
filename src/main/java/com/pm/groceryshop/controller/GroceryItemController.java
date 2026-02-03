package com.pm.groceryshop.controller;

import com.pm.groceryshop.dto.GroceryItemCreateRequestDTO;
import com.pm.groceryshop.dto.GroceryItemDTO;
import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.service.GroceryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class GroceryItemController {

    GroceryItemService groceryItemService;

    public GroceryItemController(GroceryItemService groceryItemService) {
        this.groceryItemService = groceryItemService;
    }

    @GetMapping
    public List<GroceryItemCreateRequestDTO> getAllItems(){
        return groceryItemService.getAllItems();
    }

    @GetMapping("/{id}")
    public GroceryItemCreateRequestDTO getItemById(@PathVariable Long id){
        return groceryItemService.getItemById(id);
    }
    @PostMapping
    public GroceryItemCreateRequestDTO createItem(@RequestBody GroceryItemDTO groceryItemDTO){
        return groceryItemService.createItem(groceryItemDTO);
    }
    
    @PutMapping("/{id}")
    public GroceryItemCreateRequestDTO updateItem(@RequestBody GroceryItemDTO groceryItemDTO, @PathVariable Long id){
        return groceryItemService.updateItem(groceryItemDTO,id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id){
        groceryItemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

}
