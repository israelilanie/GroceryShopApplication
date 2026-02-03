package com.pm.groceryshop.dto;

import com.pm.groceryshop.model.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    // Basic order info
    private Long id;
    private String orderNumber;        // User-facing ID (e.g., "ORD-20250114-00001")
    private OrderStatus status;
    private LocalDateTime orderDate;

    // Financial details
    private BigDecimal subtotal;       // Total before discount
    private BigDecimal discountAmount; // Discount applied
    private BigDecimal totalAmount;    // Final amount paid

    // Order items
    private List<OrderItemDTO> items;  // List of items in order
    private Integer itemCount;         // Total number of different items
    private Integer totalQuantity;     // Total units ordered

    // Metadata
    private LocalDateTime createdAt;   // When order was created (optional)

    // Computed fields (for convenience)
    private Boolean canBeCancelled;    // Based on time/status rules
    private String timeRemaining;
}
