package com.arthur.marchetti.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private final Map<String, CartItem> lines = new LinkedHashMap<>();

    public void add(MenuItem item, int qty){
        if(qty <= 0) throw new IllegalArgumentException("qty must be positive");
        lines.merge(item.getName(), new CartItem(item, qty), (oldLine, newLine) -> {
            oldLine.setQuantity(oldLine.getQuantity() + qty);
            return oldLine;
        });
    }
    public void remove(String name){ lines.remove(name); }
    public List<CartItem> items(){ return List.copyOf(lines.values()); }
    public BigDecimal subtotal(){
        return lines.values().stream()
                .map(CartItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}