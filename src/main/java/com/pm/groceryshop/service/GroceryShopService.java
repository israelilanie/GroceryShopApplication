package com.pm.groceryshop.service;

import com.pm.groceryshop.dto.CreateOrderRequest;
import com.pm.groceryshop.dto.InventoryAdjustmentRequest;
import com.pm.groceryshop.dto.OrderItemRequest;
import com.pm.groceryshop.model.CustomerOrder;
import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.model.InventoryItem;
import com.pm.groceryshop.model.OrderLineItem;
import com.pm.groceryshop.model.OrderStatus;
import com.pm.groceryshop.repository.CustomerOrderRepository;
import com.pm.groceryshop.repository.GroceryItemRepository;
import com.pm.groceryshop.repository.InventoryRepository;
import com.pm.groceryshop.service.exception.InsufficientStockException;
import com.pm.groceryshop.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroceryShopService {

    private final GroceryItemRepository groceryItemRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerOrderRepository orderRepository;

    public GroceryShopService(GroceryItemRepository groceryItemRepository,
                              InventoryRepository inventoryRepository,
                              CustomerOrderRepository orderRepository) {
        this.groceryItemRepository = groceryItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
    }

    public List<GroceryItem> listItems() {
        return groceryItemRepository.findAll();
    }

    public GroceryItem getItem(Long id) {
        return groceryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found: " + id));
    }

    @Transactional
    public GroceryItem createItem(GroceryItem item) {
        GroceryItem saved = groceryItemRepository.save(item);
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setGroceryItem(saved);
        inventoryItem.setStockQuantity(0);
        inventoryItem.setLastUpdated(Instant.now());
        inventoryRepository.save(inventoryItem);
        return saved;
    }

    @Transactional
    public GroceryItem updateItem(Long id, GroceryItem update) {
        GroceryItem item = getItem(id);
        item.setName(update.getName());
        item.setCategory(update.getCategory());
        item.setDescription(update.getDescription());
        item.setUnitPrice(update.getUnitPrice());
        return groceryItemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        GroceryItem item = getItem(id);
        inventoryRepository.findByGroceryItem(item).ifPresent(inventoryRepository::delete);
        groceryItemRepository.delete(item);
    }

    public List<InventoryItem> listInventory() {
        return inventoryRepository.findAll();
    }

    @Transactional
    public InventoryItem adjustInventory(InventoryAdjustmentRequest request) {
        GroceryItem item = getItem(request.getGroceryItemId());
        InventoryItem inventoryItem = inventoryRepository.findByGroceryItem(item)
                .orElseGet(() -> {
                    InventoryItem newItem = new InventoryItem();
                    newItem.setGroceryItem(item);
                    newItem.setStockQuantity(0);
                    return newItem;
                });
        inventoryItem.setStockQuantity(request.getQuantity());
        inventoryItem.setLastUpdated(Instant.now());
        return inventoryRepository.save(inventoryItem);
    }

    public List<CustomerOrder> listOrders() {
        return orderRepository.findAll();
    }

    public CustomerOrder getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @Transactional
    public CustomerOrder createOrder(CreateOrderRequest request) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(request.getCustomerName());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        List<OrderLineItem> lineItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            GroceryItem item = getItem(itemRequest.getGroceryItemId());
            InventoryItem inventoryItem = inventoryRepository.findByGroceryItem(item)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory missing for item: " + item.getId()));
            if (inventoryItem.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for item " + item.getName());
            }
            inventoryItem.setStockQuantity(inventoryItem.getStockQuantity() - itemRequest.getQuantity());
            inventoryItem.setLastUpdated(Instant.now());
            inventoryRepository.save(inventoryItem);

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrder(order);
            lineItem.setGroceryItem(item);
            lineItem.setQuantity(itemRequest.getQuantity());
            lineItem.setUnitPrice(item.getUnitPrice());
            lineItems.add(lineItem);
        }

        order.setItems(lineItems);
        return orderRepository.save(order);
    }

    @Transactional
    public CustomerOrder updateOrderStatus(Long id, OrderStatus status) {
        CustomerOrder order = getOrder(id);
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }
}
