package com.pm.groceryshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "grocery_items")
@Getter
@Setter
@NoArgsConstructor
public class GroceryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private BigDecimal unitPrice;

    private String category;
    private String description;

    @OneToOne(mappedBy = "groceryItem",cascade = CascadeType.ALL)
    private Inventory inventory;
}
