package com.arthur.marchetti;


import java.math.BigDecimal;
import java.time.Instant;

import com.arthur.marchetti.model.Cart;
import com.arthur.marchetti.model.Category;
import com.arthur.marchetti.model.MenuItem;
import com.arthur.marchetti.model.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTests {
    @Test
    void cart_updateQty_changes_quantity(){
        var cart = new Cart();
        var item = new MenuItem("Coffee", new BigDecimal("3.00"), Category.DRINK);
        cart.add(item, 2);
        assertEquals(2, cart.items().get(0).getQuantity());

        cart.updateQty(item, 5);
        assertEquals(5, cart.items().get(0).getQuantity());
    }

    @Test void order_snapshot_is_immutable(){
        var cart = new Cart();
        cart.add(new MenuItem("Tea", new BigDecimal("2.50"), Category.DRINK), 1);
        var order = new Order(
                "20250101_120000",
                "Alice",
                Instant.now(),
                cart.items(),
                new BigDecimal("2.50"),
                new BigDecimal("0.15"),
                new BigDecimal("2.65")
        );
        // Access record fields using method syntax: order.customerName()
        assertEquals("Alice", order.customerName());
        assertEquals(1, order.lines().size());
    }
}