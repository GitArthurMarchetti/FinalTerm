package com.arthur.marchetti.model;

import java.math.BigDecimal;

public class CartItem {
    private final MenuItem item;
    private int quantity;

    public CartItem(MenuItem item, int quantity) {
        if (item == null) throw new IllegalArgumentException("item required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        this.item = item;
        this.quantity = quantity;
    }
    public MenuItem getItem() { return item; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q){ if(q <= 0) throw new IllegalArgumentException("q must be positive"); this.quantity = q; }
    public BigDecimal lineTotal(){ return item.getPrice().multiply(BigDecimal.valueOf(quantity)); }
}