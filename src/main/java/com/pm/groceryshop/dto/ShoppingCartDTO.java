package com.pm.groceryshop.dto;

import com.pm.groceryshop.model.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShoppingCartDTO {
    private Long id;
    private String sessionId;
    private List<CartItemDTO> items;
    private Integer itemCount;
    private BigDecimal subtotal;
    private BigDecimal discountApplied;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
