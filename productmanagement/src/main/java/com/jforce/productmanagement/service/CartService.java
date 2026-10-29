package com.jforce.productmanagement.service;
import com.jforce.productmanagement.dto.CartItemResponseDto;
import com.jforce.productmanagement.entity.Cart;
import com.jforce.productmanagement.entity.CartItem;
import com.jforce.productmanagement.entity.Product;
import com.jforce.productmanagement.entity.User;
import com.jforce.productmanagement.repository.CartItemRepository;
import com.jforce.productmanagement.repository.CartRepository;
import com.jforce.productmanagement.repository.ProductRepository;
import com.jforce.productmanagement.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Constructor injection
    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Get the currently logged-in user using JWT authentication
    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String)) {

            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // Find the user's cart or create one if it does not exist
    private Cart getOrCreateCart(User user) {

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    // Convert CartItem entity to a safe DTO
// The user's password and other unnecessary information are not returned
    private CartItemResponseDto toCartItemResponse(CartItem item) {

        BigDecimal price = item.getProduct().getPrice();

        BigDecimal subtotal = price.multiply(
                BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponseDto(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                price,
                item.getQuantity(),
                subtotal
        );
    }

    // Add a product to the logged-in user's cart
    @Transactional
    public CartItemResponseDto addToCart(
            Long productId, Integer quantity) {

        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least 1");
        }

        User user = getLoggedInUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (!product.isEnabled()) {
            throw new IllegalArgumentException(
                    "Disabled products cannot be added to the cart");
        }

        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        // Increase the quantity if the product is already in the cart
        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        CartItem savedItem = cartItemRepository.save(cartItem);

        return toCartItemResponse(savedItem);
    }

    // Get all items in the logged-in user's cart
    @Transactional(readOnly = true)
    public List<CartItemResponseDto> getMyCart() {

        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        return cartItemRepository.findByCartId(cart.getId())
                .stream()
                .map(this::toCartItemResponse)
                .toList();
    }

    // Update the quantity of an item in the logged-in user's cart
    @Transactional
    public CartItemResponseDto updateCartItem(
            Long itemId, Integer quantity) {

        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least 1");
        }

        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        // Prevent users from modifying another user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException(
                    "You cannot update another user's cart item");
        }

        if (!cartItem.getProduct().isEnabled()) {
            throw new IllegalArgumentException(
                    "Disabled products cannot be purchased");
        }

        cartItem.setQuantity(quantity);

        CartItem savedItem = cartItemRepository.save(cartItem);

        return toCartItemResponse(savedItem);
    }

    // Remove an item from the logged-in user's cart
    @Transactional
    public void removeCartItem(Long itemId) {

        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        // Prevent users from removing another user's cart item
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException(
                    "You cannot remove another user's cart item");
        }

        cartItemRepository.delete(cartItem);
    }
}
