package com.pm.groceryshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    List<OrderItem> orderItems;

    public BigDecimal subtotal;
    public BigDecimal totalAmount;
    public BigDecimal discountAmount;

    public LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    public OrderStatus status;


}
