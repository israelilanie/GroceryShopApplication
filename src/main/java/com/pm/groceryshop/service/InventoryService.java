package com.pm.groceryshop.service;

import com.pm.groceryshop.Exception.itemNotFoundException;
import com.pm.groceryshop.dto.InventoryDTO;
import com.pm.groceryshop.mapper.MapperClass;
import com.pm.groceryshop.model.Inventory;
import com.pm.groceryshop.repo.GroceryItemRepository;
import com.pm.groceryshop.repo.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {


    private final InventoryRepository inventoryRepository;
    private final MapperClass mapperClass;
    private final GroceryItemService groceryItemService;
    private final GroceryItemRepository groceryItemRepository;

    public InventoryService(InventoryRepository inventoryRepository, MapperClass mapperClass, GroceryItemService groceryItemService, GroceryItemRepository groceryItemRepository) {
        this.inventoryRepository = inventoryRepository;
        this.mapperClass = mapperClass;
        this.groceryItemService = groceryItemService;
        this.groceryItemRepository = groceryItemRepository;
    }


    public boolean checkStockAvailabilityByItemId(Long itemId, Integer stock){
        Inventory inventory = inventoryRepository.findByGroceryItemId(itemId);
        Integer initStock = inventory.getStockQuantity();
        return initStock >= stock;
    }

    @Transactional
    public InventoryDTO decreaseStock(Long itemId, Integer stock){
        Inventory inventory = inventoryRepository.findByGroceryItemId(itemId);
        Inventory iv = new Inventory();
        if(!(inventory.getStockQuantity()-stock >= 0)){
            throw new RuntimeException("Impossible to decrease stock, stock not sufficient!");
        }
        else{
            Integer newQ = inventory.getStockQuantity() -stock;
            inventory.setStockQuantity(newQ);
            inventory.setLastUpdated(LocalDateTime.now());
            iv = inventoryRepository.save(inventory);
        }
        return mapperClass.toInventory(iv);
    }

    @Transactional
    public InventoryDTO increaseStock(Long itemId, Integer stock){
        Inventory inventory = inventoryRepository.findByGroceryItemId(itemId);
        Inventory iv = new Inventory();
        if(stock<=0){
            throw new RuntimeException("Impossible to increase stock, quantity invalid!");
        }
        else {
            Integer newQ = inventory.getStockQuantity() + stock;
            inventory.setStockQuantity(newQ);
            inventory.setLastUpdated(LocalDateTime.now());
            iv = inventoryRepository.save(inventory);
        }
        return mapperClass.toInventory(iv);
    }

    public List<InventoryDTO> getLowStockItem(Integer stock){
        List<Inventory> inventories = inventoryRepository.findByStockQuantityLessThan(stock);
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        for(Inventory iv : inventories){
            inventoryDTOS.add((mapperClass.toInventory(iv)));
        }
        return inventoryDTOS;
    }

    public List<InventoryDTO> getAllInventories(){
        List<Inventory> inventories = inventoryRepository.findAll();
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        for(Inventory iv : inventories){
            inventoryDTOS.add((mapperClass.toInventory(iv)));
        }
        return inventoryDTOS;
    }

    public InventoryDTO getInventoryByItemId(Long id){
        Inventory inventory = inventoryRepository.findByGroceryItemId(id);
        return mapperClass.toInventory(inventory);
    }

    @Transactional
    public InventoryDTO updateStockQuantityByItemId(Long id, Integer stock){
        Inventory inventory1 = inventoryRepository.findByGroceryItemId(id);
        inventory1.setStockQuantity(stock);
        inventory1.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory1);
        return mapperClass.toInventory(inventory1);
    }

    public boolean isStockAvailable(Long id, Integer quantity){
        Inventory inventory = inventoryRepository.findByGroceryItemId(id);
        return inventory.getStockQuantity() - quantity >= 0;
    }
}
