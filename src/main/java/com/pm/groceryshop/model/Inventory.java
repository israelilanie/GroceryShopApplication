package com.pm.groceryshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "grocery_item_id", unique = true,nullable = false)
    GroceryItem groceryItem;

    @NotNull
    @Column(nullable = false)
    private Integer stockQuantity;

    private LocalDateTime lastUpdated;

}
