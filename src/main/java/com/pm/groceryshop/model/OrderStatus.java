package com.pm.groceryshop.model;

public enum OrderStatus {
    PENDING,      // Order placed, payment processing
    CONFIRMED,    // Payment successful
    PROCESSING,   // Being prepared
    SHIPPED,      // On the way
    DELIVERED,    // Completed
    CANCELLED,    // Canceled by user/admin
    REFUNDED      // Money returned
}
