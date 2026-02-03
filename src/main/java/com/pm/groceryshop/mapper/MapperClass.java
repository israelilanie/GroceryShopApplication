package com.pm.groceryshop.mapper;

import com.pm.groceryshop.dto.*;
import com.pm.groceryshop.model.*;
import com.pm.groceryshop.service.OrderService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MapperClass {

    public GroceryItem toGroceryItem(GroceryItemDTO groceryItemDTO){
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setName(groceryItemDTO.getName());
        groceryItem.setCategory(groceryItemDTO.getCategory());
        groceryItem.setUnitPrice(groceryItemDTO.getUnitPrice());
        groceryItem.setDescription(groceryItemDTO.getDescription());
        return groceryItem;
    }

    public GroceryItemDTO toGroceryItemDTO(GroceryItem groceryItem){
        GroceryItemDTO groceryItemDTO = new GroceryItemDTO();
        groceryItemDTO.setId(groceryItem.getId());
        groceryItemDTO.setName(groceryItem.getName());
        groceryItemDTO.setCategory(groceryItem.getCategory());
        groceryItemDTO.setUnitPrice(groceryItem.getUnitPrice());
        groceryItemDTO.setDescription(groceryItem.getDescription());
        return groceryItemDTO;
    }


    public GroceryItemCreateRequestDTO groceryItemCreateRequestDTO(GroceryItem groceryItem){
        GroceryItemCreateRequestDTO groceryItemCreateRequestDTO = new GroceryItemCreateRequestDTO();
        groceryItemCreateRequestDTO.setId(groceryItem.getId());
        groceryItemCreateRequestDTO.setCategory(groceryItem.getCategory());
        groceryItemCreateRequestDTO.setDescription(groceryItem.getDescription());
        groceryItemCreateRequestDTO.setUnitPrice(groceryItem.getUnitPrice());
        groceryItemCreateRequestDTO.setName(groceryItem.getName());
        groceryItemCreateRequestDTO.setInitialStock(groceryItem.getInventory().getStockQuantity());
        return groceryItemCreateRequestDTO;
    }

    public InventoryDTO toInventory(Inventory inventory){
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setItemId(inventory.getId());
        inventoryDTO.setItemName(inventory.getGroceryItem().getName());
        inventoryDTO.setLastUpdated(inventory.getLastUpdated());
        inventoryDTO.setStockQuantity(inventory.getStockQuantity());
        return inventoryDTO;
    }

    public ShoppingCartDTO toDTO(ShoppingCart cart) {
        ShoppingCartDTO dto = new ShoppingCartDTO();

        dto.setId(cart.getId());
        dto.setSessionId(cart.getSessionId());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());

        // Defensive BigDecimal defaults
        BigDecimal totalPrice = Optional.ofNullable(cart.getTotalPrice())
                .orElse(BigDecimal.ZERO);

        BigDecimal discountApplied = Optional.ofNullable(cart.getDiscountApplied())
                .orElse(BigDecimal.ZERO);

        dto.setTotalPrice(totalPrice);
        dto.setDiscountApplied(discountApplied);

        // Map items
        List<CartItemDTO> itemDTOs = Optional.ofNullable(cart.getCartItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toCartItemDTO)
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);

        // Item count
        dto.setItemCount(itemDTOs.size());

        // Subtotal (safe)
        dto.setSubtotal(totalPrice.add(discountApplied));

        return dto;
    }


    public CartItemDTO toCartItemDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        if (item.getGroceryItem() != null) {
            dto.setItemId(item.getGroceryItem().getId());
            dto.setItemName(item.getGroceryItem().getName());
            dto.setUnitPrice(
                    Optional.ofNullable(item.getGroceryItem().getUnitPrice())
                            .orElse(BigDecimal.ZERO)
            );
            dto.setCategory(item.getGroceryItem().getCategory());

            if (item.getGroceryItem().getInventory() != null) {
                dto.setAvailableStock(
                        item.getGroceryItem().getInventory().getStockQuantity()
                );
            }
        }

        dto.setItemTotal(
                Optional.ofNullable(item.getItemTotal())
                        .orElse(BigDecimal.ZERO)
        );

        return dto;
    }

    public OrderItemDTO toOrderItemDTO(OrderItem orderItem){
        OrderItemDTO orderItemDTO = new OrderItemDTO();

        orderItemDTO.setId(orderItem.getId());
        orderItemDTO.setUnitPrice(orderItem.getUnitPrice());
        orderItemDTO.setItemTotal(orderItem.getItemTotal());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setItemName(orderItem.getItemName());


        return orderItemDTO;
    }

    public OrderDTO toOrderDTO(Order order){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderNumber(order.getOrderNumber());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setOrderDate(order.getOrderDate());

        orderDTO.setSubtotal(order.getSubtotal());
        orderDTO.setDiscountAmount(order.getDiscountAmount());
        orderDTO.setTotalAmount(order.getTotalAmount());

        List<OrderItem> orderItems = order.getOrderItems();
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();

        for(OrderItem orderItem : orderItems){
            orderItemDTOS.add(toOrderItemDTO(orderItem));
        }
        orderDTO.setItems(orderItemDTOS);
        orderDTO.setItemCount(orderItemDTOS.size());

        int quent = 0;

        for(OrderItemDTO orderItemDTO : orderItemDTOS){
            quent = quent + orderItemDTO.getQuantity();
        }
        orderDTO.setTotalQuantity(quent);

        orderDTO.setCanBeCancelled(canBeCancelled(order));
        orderDTO.setTimeRemaining(calculateTimeRemaining(order));
        orderDTO.setCreatedAt(order.getOrderDate());

        return orderDTO;
    }

    public boolean canBeCancelled(Order order){
        if (order.getStatus() == OrderStatus.CANCELLED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            return false;
        }

        LocalDateTime deadline = order.getOrderDate().plusHours(24);
        return LocalDateTime.now().isBefore(deadline);
    }

    public String calculateTimeRemaining(Order order) {
        if (!canBeCancelled(order)) {
            return "Cannot be cancelled";
        }

        LocalDateTime deadline = order.getOrderDate().plusHours(24);
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(now, deadline);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return String.format("%d hours %d minutes", hours, minutes);
    }

}
