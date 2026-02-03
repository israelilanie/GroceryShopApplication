package com.pm.groceryshop.service;

import com.pm.groceryshop.Exception.itemNotFoundException;
import com.pm.groceryshop.dto.AddProductRequest;
import com.pm.groceryshop.dto.CartItemDTO;
import com.pm.groceryshop.dto.ShoppingCartDTO;
import com.pm.groceryshop.mapper.MapperClass;
import com.pm.groceryshop.model.CartItem;
import com.pm.groceryshop.model.GroceryItem;
import com.pm.groceryshop.model.ShoppingCart;
import com.pm.groceryshop.repo.CartItemRepository;
import com.pm.groceryshop.repo.GroceryItemRepository;
import com.pm.groceryshop.repo.InventoryRepository;
import com.pm.groceryshop.repo.ShoppingCartRepository;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final InventoryService inventoryService;
    private final GroceryItemService groceryItemService;
    private final ServletRequest servletRequest;
    private final GroceryItemRepository groceryItemRepository;
    private final CartItemRepository cartItemRepository;
    private final MapperClass mapperClass;


    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, InventoryService inventoryService, GroceryItemService groceryItemService, ServletRequest servletRequest, GroceryItemRepository groceryItemRepository, CartItemRepository cartItemRepository, MapperClass mapperClass) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.inventoryService = inventoryService;
        this.groceryItemService = groceryItemService;
        this.servletRequest = servletRequest;
        this.groceryItemRepository = groceryItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.mapperClass = mapperClass;
    }

    public ShoppingCart CreateNewShoppingCart(String sessionId){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setSessionId(sessionId);
        shoppingCart.setCreatedAt(LocalDateTime.now());
        shoppingCart.setTotalPrice(BigDecimal.valueOf(0.0));
        shoppingCartRepository.save(shoppingCart);
        return shoppingCart;
    }

    public ShoppingCart getOrCreateCart(String sessionId){
        return shoppingCartRepository.findBySessionId(sessionId).orElseGet(()->CreateNewShoppingCart(sessionId));

    }

/*
    Validate quantity > 0
    Get or create cart
    Verify item exists in catalog
    Check stock availability via InventoryService
    Check if item already in cart: findByShoppingCartIdAndGroceryItemId()
    If exists: update quantity, recalculate itemTotal
    If new: create CartItem, set all fields, calculate itemTotal
    Save CartItem
    Recalculate cart totals (call private helper)
    Return updated cart

    Key calculations:

    itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity))
    */

    public ShoppingCartDTO addItemToCart(AddProductRequest addProductRequest, String sessionId){
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        CartItemDTO cartItemDTO = new CartItemDTO();
        if(!(addProductRequest.getQuantity()>0))
            throw new RuntimeException("The quantity have to be superior to zero!");
        else{
            ShoppingCart shoppingCart = getOrCreateCart(sessionId);
            GroceryItem groceryItem = groceryItemRepository.findById(addProductRequest.getGroceryItemId()).orElseThrow(()->new itemNotFoundException("Item not Found!"));

            boolean available = inventoryService.isStockAvailable(addProductRequest.getGroceryItemId(),addProductRequest.getQuantity());

            if(!available) {
                throw new RuntimeException("Insufficient Stock!");
            }
            else {
                Optional<CartItem> existingCard = cartItemRepository.findByShoppingCartIdAndGroceryItemId(shoppingCart.getId(), groceryItem.getId());
                CartItem cartItem;

                if (existingCard.isPresent()) {
                    cartItem = existingCard.get();
                    Integer currentQt = cartItem.getQuantity();
                    if(currentQt == null){
                        currentQt = 0;
                    }

                    Integer newQ = currentQt + addProductRequest.getQuantity();
                    if (!inventoryService.isStockAvailable(addProductRequest.getGroceryItemId(), newQ))
                        throw new RuntimeException("Insufficient Stock!");
                    cartItem.setQuantity(newQ);

                    BigDecimal unitPrice = groceryItem.getUnitPrice();
                    BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(newQ));
                    cartItem.setItemTotal(itemTotal);
                    cartItemRepository.save(cartItem);
                }
                else {
                    // Create new cart item
                    CartItem finalCard = new CartItem();

                    // Link to cart
                    finalCard.setShoppingCart(shoppingCart);

                    // Link to grocery item
                    finalCard.setGroceryItem(groceryItem);

                    // Set quantity
                    finalCard.setQuantity(addProductRequest.getQuantity());

                    // Calculate item total
                    BigDecimal unitPrice = groceryItem.getUnitPrice();
                    BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(addProductRequest.getQuantity()));
                    finalCard.setItemTotal(itemTotal);

                    // Save new cart item
                    cartItemRepository.save(finalCard);
            }

            }

            ShoppingCart updatedCart = recalculateCartTotals(shoppingCart);
            return mapperClass.toDTO(updatedCart);
        }

    }

    private ShoppingCart recalculateCartTotals(ShoppingCart cart) {
        // Reload cart with all items (avoid stale data)
        cart = shoppingCartRepository.findByIdWithItems(cart.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Sum all item totals
        BigDecimal subtotal = cart.getCartItems().stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate discount
        BigDecimal discount = subtotal.multiply(BigDecimal.valueOf(0.2) );

        // Calculate final total
        BigDecimal total = subtotal.subtract(discount);

        // Update cart
        cart.setTotalPrice(total);
        cart.setDiscountApplied(discount);
        cart.setUpdatedAt(LocalDateTime.now());

        // Save
        return shoppingCartRepository.save(cart);
    }

    public List<ShoppingCartDTO> getAllShoppingCart(){
        List<ShoppingCart> carts = shoppingCartRepository.findAll();
        return carts.stream()
                .map(mapperClass::toDTO)
                .collect(Collectors.toList());
    }

    public ShoppingCartDTO getShoppingCartBySessionId(String sessionId){
        ShoppingCart shoppingCart = shoppingCartRepository.findBySessionId(sessionId).orElseThrow();
        return mapperClass.toDTO(shoppingCart);
    }


    public ShoppingCartDTO removeCard(String sessionId, Long id){
        ShoppingCart shoppingCart = shoppingCartRepository.findBySessionId(sessionId).orElseThrow(()->new RuntimeException("Card Not found!"));

        CartItem cartItem = cartItemRepository.findByShoppingCartIdAndGroceryItemId(shoppingCart.getId(),id ).orElseThrow();
        cartItemRepository.delete(cartItem);
        ShoppingCart shoppingCart1 = recalculateCartTotals(shoppingCart);
        return mapperClass.toDTO(shoppingCart1);
    }


    public void clearCartPattern(String sessionId){
        ShoppingCart shoppingCart = shoppingCartRepository.findBySessionId(sessionId).orElseThrow();
        shoppingCartRepository.delete(shoppingCart);
    }
}
