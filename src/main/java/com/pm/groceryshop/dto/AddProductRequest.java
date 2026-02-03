package com.pm.groceryshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddProductRequest {
    private String sessionId;
    private Long groceryItemId;
    private int quantity;
}
