package com.jforce.productmanagement.service;
import com.jforce.productmanagement.entity.*;
import com.jforce.productmanagement.repository.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            AddressRepository addressRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String)) {
            throw new RuntimeException("User is not authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public Order placeOrder(Long addressId) {

        User user = getLoggedInUser();

        Address address = addressRepository
                .findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Address not found for the logged-in user"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place an order with an empty cart");
        }

        // Validate every cart item before changing inventory.
        for (CartItem item : cartItems) {

            Product product = item.getProduct();

            if (!product.isEnabled()) {
                throw new RuntimeException(
                        "Product is disabled: " + product.getName());
            }

            Inventory inventory = inventoryRepository
                    .findByProductId(product.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Inventory not found for " + product.getName()));

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient inventory for " + product.getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            Inventory inventory = inventoryRepository
                    .findByProductId(product.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Inventory not found"));

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);

            inventory.setQuantity(
                    inventory.getQuantity() - cartItem.getQuantity());

            inventoryRepository.save(inventory);
        }

        orderItemRepository.saveAll(orderItems);

        savedOrder.setTotalAmount(totalAmount);
        Order finalOrder = orderRepository.save(savedOrder);

        // Clear the cart after successful order creation.
        cartItemRepository.deleteAll(cartItems);

        return finalOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getMyOrders() {

        User user = getLoggedInUser();

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional(readOnly = true)
    public Order getMyOrderById(Long orderId) {
        // Get the currently logged-in user
        User user = getLoggedInUser();
        // Find the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));
        // Verify that the order belongs to the logged-in user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You cannot access another user's order");
        }

        return order;
    }
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

}

