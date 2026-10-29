package com.jforce.productmanagement.repository;

import com.jforce.productmanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
    // Retrieve all orders belonging to a user, newest first
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}

