package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.dto.CartItemResponseDto;
import com.jforce.productmanagement.service.CartService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 1. Add a product to the logged-in user's cart
    @PostMapping("/items")
    public ResponseEntity<CartItemResponseDto> addToCart(
            @RequestParam Long productId,
            @RequestParam @NotNull @Min(1) Integer quantity) {

        CartItemResponseDto response =
                cartService.addToCart(productId, quantity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get all items in the logged-in user's cart
    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> getMyCart() {

        List<CartItemResponseDto> cartItems =
                cartService.getMyCart();

        return ResponseEntity.ok(cartItems);
    }

    // 3. Update the quantity of a cart item
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItemResponseDto> updateCartItem(
            @PathVariable Long itemId,
            @RequestParam @NotNull @Min(1) Integer quantity) {

        CartItemResponseDto response =
                cartService.updateCartItem(itemId, quantity);

        return ResponseEntity.ok(response);
    }

    // 4. Remove an item from the logged-in user's cart
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long itemId) {

        cartService.removeCartItem(itemId);

        return ResponseEntity.noContent().build();
    }
}
