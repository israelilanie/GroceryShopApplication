package com.pm.groceryshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.groceryshop.dto.InventoryAdjustmentRequest;
import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.model.InventoryItem;
import com.pm.groceryshop.service.GroceryShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private GroceryShopService groceryShopService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        InventoryController controller = new InventoryController(groceryShopService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listInventory_returnsData() throws Exception {
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setId(1L);
        groceryItem.setName("Rice");
        groceryItem.setCategory("Grains");
        groceryItem.setUnitPrice(BigDecimal.valueOf(4.1));

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setId(10L);
        inventoryItem.setGroceryItem(groceryItem);
        inventoryItem.setStockQuantity(100);

        when(groceryShopService.listInventory()).thenReturn(List.of(inventoryItem));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").value(100));
    }

    @Test
    void adjustInventory_withInvalidPayload_returnsBadRequest() throws Exception {
        String invalid = """
                {
                  "groceryItemId": null,
                  "quantity": -3
                }
                """;

        mockMvc.perform(post("/api/inventory/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.groceryItemId").exists())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    void adjustInventory_validPayload_returnsAdjusted() throws Exception {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest();
        request.setGroceryItemId(1L);
        request.setQuantity(50);

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setId(33L);
        inventoryItem.setStockQuantity(50);

        when(groceryShopService.adjustInventory(any(InventoryAdjustmentRequest.class))).thenReturn(inventoryItem);

        mockMvc.perform(post("/api/inventory/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(33))
                .andExpect(jsonPath("$.stockQuantity").value(50));
    }
}
