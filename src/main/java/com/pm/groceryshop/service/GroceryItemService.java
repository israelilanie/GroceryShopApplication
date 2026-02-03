package com.pm.groceryshop.service;

import com.pm.groceryshop.Exception.itemNotFoundException;
import com.pm.groceryshop.dto.GroceryItemCreateRequestDTO;
import com.pm.groceryshop.dto.GroceryItemDTO;
import com.pm.groceryshop.mapper.MapperClass;
import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.model.Inventory;
import com.pm.groceryshop.repo.GroceryItemRepository;
import com.pm.groceryshop.repo.InventoryRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class GroceryItemService {


    private final GroceryItemRepository groceryItemRepository;
    private final InventoryRepository inventoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(GroceryItemService.class);
    private final MapperClass mapperClass;

    public GroceryItemService(GroceryItemRepository groceryItemRepository, InventoryRepository inventoryRepository, MapperClass mapperClass) {
        this.groceryItemRepository = groceryItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.mapperClass = mapperClass;
    }


    public GroceryItemCreateRequestDTO createItem(GroceryItemDTO groceryItemDTO){
                GroceryItem groceryItem = mapperClass.toGroceryItem(groceryItemDTO);

                Inventory inventory = new Inventory();
                groceryItem.setId(null);
                GroceryItem created = groceryItemRepository.save(groceryItem);

                inventory.setGroceryItem(created);
                inventory.setStockQuantity(0);  // Or default value
                inventory.setLastUpdated(LocalDateTime.now());
                Inventory saved = inventoryRepository.save(inventory);

                created.setInventory(saved);
        return mapperClass.groceryItemCreateRequestDTO(created);
    }

    public GroceryItemCreateRequestDTO getItemById(Long id){
        GroceryItem item = groceryItemRepository.findById(id).orElse(new GroceryItem());
        return mapperClass.groceryItemCreateRequestDTO(item);
    }

    public List<GroceryItemCreateRequestDTO> getAllItems(){
        List<GroceryItem> items = new ArrayList<>();
        items = groceryItemRepository.findAll();
        List<GroceryItemCreateRequestDTO> dos = new ArrayList<>();

        for(GroceryItem item:items){
            dos.add(mapperClass.groceryItemCreateRequestDTO(item));
        }
        return dos;
    }

    public GroceryItemCreateRequestDTO updateItem(GroceryItemDTO groceryItemDTO, Long id){
            GroceryItem groceryItem = groceryItemRepository.findById(id).orElseThrow(()-> new itemNotFoundException("item not found with Id:"+id));

            groceryItem.setDescription(groceryItemDTO.getDescription());
            groceryItem.setName(groceryItemDTO.getName());
            groceryItem.setCategory(groceryItemDTO.getCategory());
            groceryItem.setUnitPrice(groceryItemDTO.getUnitPrice());
            GroceryItem updated = groceryItemRepository.save(groceryItem);

            return mapperClass.groceryItemCreateRequestDTO(updated);

    }

    public void deleteItem(Long id){
        if(groceryItemRepository.existsById(id)){
            groceryItemRepository.deleteById(id);
        }
    }


}
