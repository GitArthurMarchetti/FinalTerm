package com.arthur.marchetti.services;


import com.arthur.marchetti.interfaces.TaxCalculator.TaxCalculator;
import com.arthur.marchetti.model.Cart;

import java.math.BigDecimal;
import java.util.List;

public class ReceiptService {
    private final TaxCalculator taxCalc;
    public ReceiptService(TaxCalculator taxCalc){ this.taxCalc = taxCalc; }

    public List<String> render(Cart cart){
        var lines = new java.util.ArrayList<String>();
        lines.add("Item                 Qty   Price   Total");
        for(var ci : cart.items()){
            String line = String.format("%-20s %3d %7.2f %7.2f",
                    ci.getItem().getName(), ci.getQuantity(),
                    ci.getItem().getPrice(), ci.lineTotal());
            lines.add(line);
        }
        BigDecimal sub = cart.getSubtotal();
        BigDecimal tax = taxCalc.tax(sub);
        BigDecimal grand = sub.add(tax);
        lines.add("----------------------------------------");
        lines.add(String.format("%-27s %10.2f","Subtotal:", sub));
        lines.add(String.format("%-27s %10.2f","Tax:", tax));
        lines.add(String.format("%-27s %10.2f","Total:", grand));
        return lines;
    }
}