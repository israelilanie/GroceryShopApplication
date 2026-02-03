package com.pm.groceryshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class GroceryItemDTO {
    private Long id;
    private String name;
    private BigDecimal unitPrice;
    private String category;
    private String description;
    private Integer initialStock;

    public GroceryItemDTO(Long id, String description, String name, BigDecimal unitPrice, String category, Integer initialStock) {
        this.id =id;
        this.description = description;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
        this.initialStock = initialStock;
    }

}


