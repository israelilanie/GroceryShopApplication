package com.pm.groceryshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.groceryshop.model.GroceryItem;
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
class GroceryItemControllerTest {

    @Mock
    private GroceryShopService groceryShopService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        GroceryItemController controller = new GroceryItemController(groceryShopService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listItems_returnsItems() throws Exception {
        GroceryItem item = new GroceryItem();
        item.setId(1L);
        item.setName("Milk");
        item.setCategory("Dairy");
        item.setUnitPrice(BigDecimal.valueOf(3.10));

        when(groceryShopService.listItems()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Milk"));
    }

    @Test
    void createItem_withInvalidPayload_returnsBadRequest() throws Exception {
        String invalid = """
                {
                  "name": "",
                  "category": "",
                  "unitPrice": -1
                }
                """;

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.category").exists())
                .andExpect(jsonPath("$.errors.unitPrice").exists());
    }

    @Test
    void createItem_withValidPayload_returnsCreated() throws Exception {
        GroceryItem item = new GroceryItem();
        item.setId(7L);
        item.setName("Bread");
        item.setCategory("Bakery");
        item.setUnitPrice(BigDecimal.valueOf(2.0));

        when(groceryShopService.createItem(any(GroceryItem.class))).thenReturn(item);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7));
    }
}
