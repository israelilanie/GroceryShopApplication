package com.pm.groceryshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;

    // Snapshot data (as it was at time of order)
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal itemTotal;

}
