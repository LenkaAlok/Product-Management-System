package com.jforce.productmanagement.dto;
import java.math.BigDecimal;

public record CartItemResponseDto(
        Long cartItemId,
        Long productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {
}
