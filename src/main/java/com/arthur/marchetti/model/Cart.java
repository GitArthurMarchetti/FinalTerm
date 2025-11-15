package com.arthur.marchetti.model;

import com.arthur.marchetti.interfaces.TaxCalculator.TaxCalculator;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private final Map<String, CartItem> lines = new LinkedHashMap<>();

    public void add(MenuItem item, int qty){
        if(qty <= 0) throw new IllegalArgumentException("qty must be positive");

        lines.merge(
                item.getName(),
                new CartItem(item, qty),
                (oldLine, newLine) -> {
                    oldLine.setQuantity(oldLine.getQuantity() + qty);
                    return oldLine;
                }
        );
    }

    public void remove(String name){
        lines.remove(name);
    }

    public List<CartItem> items(){
        return List.copyOf(lines.values());
    }

    public void clear(){
        lines.clear();
    }

    public boolean isEmpty(){
        return lines.isEmpty();
    }

    public BigDecimal getSubtotal(){
        return lines.values().stream()
                .map(CartItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTax(TaxCalculator taxCalculator){
        if(taxCalculator == null) throw new IllegalArgumentException("taxCalculator required");
        return taxCalculator.tax(getSubtotal());
    }

    public BigDecimal getTotal(TaxCalculator taxCalculator){
        return getSubtotal().add(getTax(taxCalculator));
    }

    public void updateQty(MenuItem item, int qty){
        if(item == null) throw new IllegalArgumentException("item required");
        if(!lines.containsKey(item.getName())){
            throw new IllegalArgumentException("item not in cart");
        }
        lines.computeIfPresent(item.getName(), (k, line) -> {
            line.setQuantity(qty);
            return line;
        });
    }
}

