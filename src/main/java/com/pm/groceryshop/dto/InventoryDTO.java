package com.pm.groceryshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryDTO {
    private Long itemId;
    private String itemName;
    private Integer stockQuantity;
    private LocalDateTime lastUpdated;
}

