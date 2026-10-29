package com.jforce.productmanagement.service;
import com.jforce.productmanagement.entity.Inventory;
import com.jforce.productmanagement.entity.Product;
import com.jforce.productmanagement.repository.InventoryRepository;
import com.jforce.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Inventory not found with ID: " + id));
    }


    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException("Inventory not found for product ID: " + productId));
    }

    // Create inventory for a product
    public Inventory createInventory(Long productId, Integer quantity) {

        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with ID: " + productId));

        if (inventoryRepository.existsByProductId(productId)) {
            throw new RuntimeException("Inventory already exists for this product");
        }

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(quantity);

        return inventoryRepository.save(inventory);
    }

    // Update inventory quantity
    public Inventory updateInventory(Long inventoryId, Integer quantity) {

        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }

        Inventory inventory = getInventoryById(inventoryId);
        inventory.setQuantity(quantity);

        return inventoryRepository.save(inventory);
    }

    // Reduce stock after a successful order
    @Transactional
    public void reduceStock(Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Order quantity must be greater than zero");
        }

        Inventory inventory = getInventoryByProductId(productId);

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory for product ID: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
}