package com.pm.groceryshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private String category;
    private Integer quantity;
    private BigDecimal itemTotal;
    private Integer availableStock;
}
