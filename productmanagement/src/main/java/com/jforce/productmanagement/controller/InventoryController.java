package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.dto.InventoryRequestDto;
import com.jforce.productmanagement.entity.Inventory;
import com.jforce.productmanagement.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(
            @Valid @RequestBody InventoryRequestDto request) {

        Inventory inventory = inventoryService.createInventory(
                request.productId(),
                request.quantity()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }

    // Get all inventory
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    // Get inventory by inventory ID
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    // Get inventory by product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
                inventoryService.getInventoryByProductId(productId));
    }

    // Create inventory for a product

    // Update inventory quantity
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long id,
            @RequestParam @Min(value = 0, message = "Quantity cannot be negative")
            Integer quantity) {

        Inventory inventory = inventoryService.updateInventory(id, quantity);

        return ResponseEntity.ok(inventory);
    }
}