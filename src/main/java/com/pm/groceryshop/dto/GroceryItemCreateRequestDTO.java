package com.pm.groceryshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Setter
@Getter
public class GroceryItemCreateRequestDTO {
    private Long id;
    private String name;
    private BigDecimal unitPrice;
    private String category;
    private String description;
    private Integer initialStock;

    public GroceryItemCreateRequestDTO(Long id, String name, BigDecimal unitPrice, String category, String description, Integer initialStock) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
        this.description = description;
        this.initialStock = initialStock;
    }
}
