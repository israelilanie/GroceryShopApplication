package com.pm.groceryshop.service;

import com.pm.groceryshop.dto.CreateOrderRequest;
import com.pm.groceryshop.dto.InventoryAdjustmentRequest;
import com.pm.groceryshop.dto.OrderItemRequest;
import com.pm.groceryshop.model.CustomerOrder;
import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.model.InventoryItem;
import com.pm.groceryshop.model.OrderStatus;
import com.pm.groceryshop.repository.CustomerOrderRepository;
import com.pm.groceryshop.repository.GroceryItemRepository;
import com.pm.groceryshop.repository.InventoryRepository;
import com.pm.groceryshop.service.exception.InsufficientStockException;
import com.pm.groceryshop.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroceryShopServiceTest {

    @Mock
    private GroceryItemRepository groceryItemRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private CustomerOrderRepository orderRepository;

    @InjectMocks
    private GroceryShopService groceryShopService;

    private GroceryItem groceryItem;

    @BeforeEach
    void setUp() {
        groceryItem = new GroceryItem();
        groceryItem.setId(1L);
        groceryItem.setName("Apple");
        groceryItem.setCategory("Fruit");
        groceryItem.setDescription("Red apple");
        groceryItem.setUnitPrice(BigDecimal.valueOf(2.50));
    }

    @Test
    void getItem_whenFound_returnsItem() {
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));

        GroceryItem result = groceryShopService.getItem(1L);

        assertEquals("Apple", result.getName());
    }

    @Test
    void getItem_whenMissing_throwsResourceNotFound() {
        when(groceryItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groceryShopService.getItem(99L));
    }

    @Test
    void createItem_createsInventoryWithZeroStock() {
        when(groceryItemRepository.save(groceryItem)).thenReturn(groceryItem);
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GroceryItem created = groceryShopService.createItem(groceryItem);

        assertEquals(groceryItem, created);
        verify(inventoryRepository).save(any(InventoryItem.class));
    }

    @Test
    void adjustInventory_whenInventoryMissing_createsNewInventory() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest();
        request.setGroceryItemId(1L);
        request.setQuantity(12);

        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(inventoryRepository.findByGroceryItem(groceryItem)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryItem result = groceryShopService.adjustInventory(request);

        assertEquals(12, result.getStockQuantity());
        assertNotNull(result.getLastUpdated());
    }

    @Test
    void createOrder_whenStockEnough_updatesInventoryAndSavesOrder() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setGroceryItemId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Patrick");
        request.setItems(List.of(itemRequest));

        InventoryItem stock = new InventoryItem();
        stock.setGroceryItem(groceryItem);
        stock.setStockQuantity(5);

        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(inventoryRepository.findByGroceryItem(groceryItem)).thenReturn(Optional.of(stock));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerOrder order = groceryShopService.createOrder(request);

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(3, stock.getStockQuantity());
        verify(inventoryRepository).save(stock);
    }

    @Test
    void createOrder_whenStockInsufficient_throwsException() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setGroceryItemId(1L);
        itemRequest.setQuantity(10);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Patrick");
        request.setItems(List.of(itemRequest));

        InventoryItem stock = new InventoryItem();
        stock.setGroceryItem(groceryItem);
        stock.setStockQuantity(2);

        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(inventoryRepository.findByGroceryItem(groceryItem)).thenReturn(Optional.of(stock));

        assertThrows(InsufficientStockException.class, () -> groceryShopService.createOrder(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_changesStatusAndSaves() {
        CustomerOrder order = new CustomerOrder();
        order.setId(9L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        CustomerOrder result = groceryShopService.updateOrderStatus(9L, OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        assertNotNull(result.getUpdatedAt());
    }
}
