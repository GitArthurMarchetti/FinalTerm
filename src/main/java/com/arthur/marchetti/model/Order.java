package com.arthur.marchetti.model;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record Order(
        String orderId,
        String customerName,
        Instant timestamp,
        List<CartItem> lines,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total
) {
    // Compact constructor: runs validation before fields are assigned
    public Order {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId required");
        }
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("customerName required");
        }

        // Make a defensive copy of the cart lines to ensure immutability
        lines = lines.stream()
                .map(li -> new CartItem(li.getItem(), li.getQuantity()))
                .toList();
        lines = List.copyOf(lines);
    }
}
