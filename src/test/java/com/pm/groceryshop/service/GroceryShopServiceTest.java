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
    void listItems_returnsRepositoryItems() {
        when(groceryItemRepository.findAll()).thenReturn(List.of(groceryItem));

        assertEquals(List.of(groceryItem), groceryShopService.listItems());
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
    void updateItem_replacesAllEditableFields() {
        GroceryItem update = new GroceryItem();
        update.setName("Pear");
        update.setCategory("Fresh fruit");
        update.setDescription("Green pear");
        update.setUnitPrice(BigDecimal.valueOf(3.25));
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(groceryItemRepository.save(groceryItem)).thenReturn(groceryItem);

        GroceryItem result = groceryShopService.updateItem(1L, update);

        assertEquals("Pear", result.getName());
        assertEquals("Fresh fruit", result.getCategory());
        assertEquals("Green pear", result.getDescription());
        assertEquals(BigDecimal.valueOf(3.25), result.getUnitPrice());
    }

    @Test
    void deleteItem_deletesAttachedInventoryThenItem() {
        InventoryItem inventory = new InventoryItem();
        inventory.setGroceryItem(groceryItem);
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(inventoryRepository.findByGroceryItem(groceryItem)).thenReturn(Optional.of(inventory));

        groceryShopService.deleteItem(1L);

        verify(inventoryRepository).delete(inventory);
        verify(groceryItemRepository).delete(groceryItem);
    }

    @Test
    void deleteItem_withoutInventory_deletesItem() {
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(inventoryRepository.findByGroceryItem(groceryItem)).thenReturn(Optional.empty());

        groceryShopService.deleteItem(1L);

        verify(inventoryRepository, never()).delete(any());
        verify(groceryItemRepository).delete(groceryItem);
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
    void adjustInventory_updatesExistingInventory() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest();
        request.setGroceryItemId(1L);
        request.setQuantity(8);
        InventoryItem inventory = new InventoryItem();
        inventory.setStockQuantity(2);
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(inventoryRepository.findByGroceryItem(groceryItem)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        assertEquals(8, groceryShopService.adjustInventory(request).getStockQuantity());
    }

    @Test
    void listInventoryAndOrders_returnRepositoryData() {
        CustomerOrder order = new CustomerOrder();
        when(inventoryRepository.findAll()).thenReturn(List.of());
        when(orderRepository.findAll()).thenReturn(List.of(order));

        assertTrue(groceryShopService.listInventory().isEmpty());
        assertEquals(List.of(order), groceryShopService.listOrders());
    }

    @Test
    void getOrder_whenMissing_throwsResourceNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groceryShopService.getOrder(99L));
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
    void createOrder_whenInventoryMissing_throwsResourceNotFound() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setGroceryItemId(1L);
        itemRequest.setQuantity(1);
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Patrick");
        request.setItems(List.of(itemRequest));
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(groceryItem));
        when(inventoryRepository.findByGroceryItem(groceryItem)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groceryShopService.createOrder(request));
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
