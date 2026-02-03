package com.pm.groceryshop.controller;

import com.pm.groceryshop.dto.CreateOrderRequest;
import com.pm.groceryshop.dto.OrderStatusUpdateRequest;
import com.pm.groceryshop.model.CustomerOrder;
import com.pm.groceryshop.service.GroceryShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final GroceryShopService groceryShopService;

    public OrderController(GroceryShopService groceryShopService) {
        this.groceryShopService = groceryShopService;
    }

    @GetMapping
    public List<CustomerOrder> listOrders() {
        return groceryShopService.listOrders();
    }

    @GetMapping("/{id}")
    public CustomerOrder getOrder(@PathVariable Long id) {
        return groceryShopService.getOrder(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return groceryShopService.createOrder(request);
    }

    @PutMapping("/{id}/status")
    public CustomerOrder updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return groceryShopService.updateOrderStatus(id, request.getStatus());
    }
}
