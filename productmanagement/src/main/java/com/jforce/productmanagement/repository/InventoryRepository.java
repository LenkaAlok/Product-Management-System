package com.jforce.productmanagement.repository;
import com.jforce.productmanagement.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
    // Check whether inventory already exists for a product
    boolean existsByProductId(Long productId);
}

