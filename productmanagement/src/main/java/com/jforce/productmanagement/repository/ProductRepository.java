package com.jforce.productmanagement.repository;
import com.jforce.productmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByEnabledTrue();

    List<Product> findByCategory_IdAndEnabledTrue(Long categoryId);
}


