package com.pm.groceryshop.controller;

import com.pm.groceryshop.dto.OrderDTO;
import com.pm.groceryshop.model.OrderStatus;
import com.pm.groceryshop.service.OrderService;
import com.pm.groceryshop.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final ShoppingCartService shoppingCartService;

    public OrderController(OrderService orderService, ShoppingCartService shoppingCartService) {
        this.orderService = orderService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/all")
    public List<OrderDTO> getAllOrders(){
        return orderService.findAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderDTO getOrderById(@PathVariable Long orderId){
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/number/{orderNum}")
    public OrderDTO getByOrderNumber(@PathVariable String orderNum){
        return orderService.findByOrderNumber(orderNum);
    }

    @PostMapping("/checkout")
    public OrderDTO checkOut(HttpSession session){
        String sessionId = session.getId();
        return orderService.checkOut(sessionId);
    }

    @PutMapping("/cancel/{orderId}")
    public void cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
    }

    @GetMapping("/status/{orderStatus}")
    public List<OrderDTO> getAllByStatus(@PathVariable OrderStatus orderStatus){
        return orderService.findByStatus(orderStatus);
    }
}
