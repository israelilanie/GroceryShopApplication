package com.pm.groceryshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.groceryshop.dto.CreateOrderRequest;
import com.pm.groceryshop.dto.OrderItemRequest;
import com.pm.groceryshop.dto.OrderStatusUpdateRequest;
import com.pm.groceryshop.model.CustomerOrder;
import com.pm.groceryshop.model.OrderStatus;
import com.pm.groceryshop.service.GroceryShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private GroceryShopService groceryShopService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        OrderController controller = new OrderController(groceryShopService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void createOrder_validRequest_returnsCreated() throws Exception {
        CustomerOrder savedOrder = new CustomerOrder();
        savedOrder.setId(15L);
        savedOrder.setCustomerName("Alice");
        savedOrder.setStatus(OrderStatus.PENDING);

        OrderItemRequest line = new OrderItemRequest();
        line.setGroceryItemId(1L);
        line.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Alice");
        request.setItems(List.of(line));

        when(groceryShopService.createOrder(any(CreateOrderRequest.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.customerName").value("Alice"));
    }

    @Test
    void updateStatus_returnsUpdatedOrder() throws Exception {
        CustomerOrder updated = new CustomerOrder();
        updated.setId(22L);
        updated.setStatus(OrderStatus.DELIVERED);

        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(OrderStatus.DELIVERED);

        when(groceryShopService.updateOrderStatus(eq(22L), eq(OrderStatus.DELIVERED))).thenReturn(updated);

        mockMvc.perform(put("/api/orders/22/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }
}
