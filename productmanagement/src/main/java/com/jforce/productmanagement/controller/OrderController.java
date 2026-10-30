package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.entity.Order;

import com.jforce.productmanagement.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Place an order using the logged-in user's cart.
    @PostMapping
    public ResponseEntity<Order> placeOrder(
            @RequestParam Long addressId) {

        Order order = orderService.placeOrder(addressId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // Get the logged-in user's orders.
    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    // SUPER_ADMIN: Get all orders in the system.
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/super-admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Get one order belonging to the logged-in user.
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getMyOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.getMyOrderById(orderId));
    }
}

