package com.pm.groceryshop.controller;

import com.pm.groceryshop.dto.AddProductRequest;
import com.pm.groceryshop.dto.ShoppingCartDTO;
import com.pm.groceryshop.mapper.MapperClass;
import com.pm.groceryshop.model.ShoppingCart;
import com.pm.groceryshop.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService service;
    private final MapperClass mapperClass;

    public ShoppingCartController(ShoppingCartService service, MapperClass mapperClass) {
        this.service = service;
        this.mapperClass = mapperClass;
    }

    @GetMapping
    public ResponseEntity<ShoppingCartDTO>  getCart(HttpSession session) {
        String sessionId = session.getId();
        ShoppingCart shoppingCart = service.getOrCreateCart(sessionId);
        ShoppingCartDTO shoppingCartDTO = mapperClass.toDTO(shoppingCart);
        return ResponseEntity.ok(shoppingCartDTO);
    }
    @PostMapping
    public ResponseEntity<ShoppingCartDTO> addItemToCart(
            @Valid @RequestBody AddProductRequest request,
            HttpSession session) {

        String sessionId = session.getId();
        ShoppingCartDTO cart = service.addItemToCart(request, sessionId);  // ✅ DTO
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <ShoppingCartDTO> removeCard(@PathVariable Long id, HttpSession session){
        String sessionId = session.getId();
        ShoppingCartDTO shoppingCart = service.removeCard(sessionId,id);
        return ResponseEntity.ok(shoppingCart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(HttpSession session) {
        String sessionId = session.getId();
        service.clearCartPattern(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}")
    public ShoppingCartDTO getShoppingCartBySessionId(@PathVariable String sessionId){
        return service.getShoppingCartBySessionId(sessionId);
    }

    @GetMapping("/all")
    public List<ShoppingCartDTO> getAllShoppingCart (){
        return service.getAllShoppingCart();
    }
}