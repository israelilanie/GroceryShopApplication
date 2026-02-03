package com.pm.groceryshop.service;

import com.pm.groceryshop.dto.OrderDTO;
import com.pm.groceryshop.dto.ShoppingCartDTO;
import com.pm.groceryshop.mapper.MapperClass;
import com.pm.groceryshop.model.*;
import com.pm.groceryshop.repo.GroceryItemRepository;
import com.pm.groceryshop.repo.OrderItemRepository;
import com.pm.groceryshop.repo.OrderRepository;
import com.pm.groceryshop.repo.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private OrderItemRepository orderItemRepository;
    private ShoppingCartService shoppingCartService;
    private MapperClass mapperClass;
    private ShoppingCartRepository shoppingCartRepository;
    private InventoryService inventoryService;
    private OrderRepository orderRepository;
    private GroceryItemRepository groceryItemRepository;

    public OrderService(OrderItemRepository orderItemRepository, ShoppingCartService shoppingCartService, MapperClass mapperClass, ShoppingCartRepository shoppingCartRepository, InventoryService inventoryService, OrderRepository orderRepository, GroceryItemRepository groceryItemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.mapperClass = mapperClass;
        this.shoppingCartRepository = shoppingCartRepository;
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
        this.groceryItemRepository = groceryItemRepository;
    }

    @Transactional
    public OrderDTO checkOut(String sessionId) {

        ShoppingCart shoppingCart = shoppingCartRepository.findBySessionId(sessionId).orElseThrow();
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new RuntimeException("The card Has no items on it");
        }
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            if (!inventoryService.checkStockAvailabilityByItemId(cartItem.getGroceryItem().getId(), cartItem.getQuantity()))
                throw new RuntimeException("Insufficient amount of stock for items!");
        }
        Order order = new Order();
        LocalDateTime currentDate = LocalDateTime.now();

        String orderNumber = generateOrderNumber();
        ShoppingCartDTO shoppingCartDTO = mapperClass.toDTO(shoppingCart);

        order.setOrderDate(currentDate);
        order.setStatus(OrderStatus.PROCESSING);
        order.setOrderNumber(orderNumber);
        order.setSubtotal(shoppingCartDTO.getSubtotal());
        order.setTotalAmount(shoppingCartDTO.getTotalPrice());
        order.setDiscountAmount(shoppingCartDTO.getDiscountApplied());

        List<OrderItem> itemList = new ArrayList<>();

        for(CartItem cartItem:shoppingCart.getCartItems()){
            OrderItem orderItem1 = new OrderItem();
            orderItem1.setItemName(cartItem.getGroceryItem().getName());
            orderItem1.setUnitPrice(cartItem.getGroceryItem().getUnitPrice());
            orderItem1.setQuantity(cartItem.getQuantity());
            orderItem1.setItemTotal(cartItem.getItemTotal());
            orderItem1.setOrder(order);
            inventoryService.decreaseStock(cartItem.getGroceryItem().getId(),cartItem.getQuantity());
            itemList.add(orderItem1);
        }
        order.setOrderItems(itemList);
        orderRepository.save(order);

        shoppingCartService.clearCartPattern(sessionId);

        return mapperClass.toOrderDTO(order);
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("ORD-%s-%s", datePart, uniqueId);
        // Result: ORD-20250114-A1B2C3D4
    }

    @Transactional
    public OrderDTO findByOrderNumber(String orderNumber){
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow();
        return mapperClass.toOrderDTO(order);
    }

    @Transactional
    public OrderDTO getOrderById(Long orderId){
        Order order = orderRepository.findByIdWithItems(orderId).orElseThrow();
        return mapperClass.toOrderDTO(order);
    }
    @Transactional
    public List<OrderDTO> findAllOrders(){
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        List<OrderDTO> orderDTOS = new ArrayList<>();

        for(Order order : orders){
            orderDTOS.add(mapperClass.toOrderDTO(order));
        }
        return orderDTOS;
    }

    @Transactional
    List<OrderDTO> findByOrderDateBetween(LocalDateTime start, LocalDateTime end){
        List<Order> orders = orderRepository.findByOrderDateBetween(start,end);
        List<OrderDTO> orderDTOS = new ArrayList<>();

        for(Order order : orders){
            orderDTOS.add(mapperClass.toOrderDTO(order));
        }
        return orderDTOS;
    }

    @Transactional
    public List<OrderDTO> findByStatus(OrderStatus status){
        List<Order> orders = orderRepository.findByStatus(status);
        List<OrderDTO> orderDTOS = new ArrayList<>();

        for(Order order : orders){
            orderDTOS.add(mapperClass.toOrderDTO(order));
        }
        return orderDTOS;
    }

    @Transactional
    List<OrderDTO> findByStatusAndOrderDateBetween( OrderStatus status, LocalDateTime start, LocalDateTime end){
        List<Order> orders = orderRepository.findByStatusAndOrderDateBetween(status,start,end);
        List<OrderDTO> orderDTOS = new ArrayList<>();

        for(Order order : orders){
            orderDTOS.add(mapperClass.toOrderDTO(order));
        }
        return orderDTOS;
    }

    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow();

        if(order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new RuntimeException("Order Already Canceled!");
        }
        LocalDateTime deadline = order.getOrderDate().plusHours(24);

        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cancellation period expired");
        }



        for(OrderItem orderItem : order.getOrderItems()){
            GroceryItem groceryItem = groceryItemRepository.findGroceryItemByName(orderItem.getItemName()).orElseThrow(()->new RuntimeException("Item with this name not found!"));
            inventoryService.increaseStock(groceryItem.getId(), orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }


    public Page<Order> getOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAllByOrderByOrderDateDesc(pageable);
    }


}
