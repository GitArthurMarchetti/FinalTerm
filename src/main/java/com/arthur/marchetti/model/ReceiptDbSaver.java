package com.arthur.marchetti.model;


import com.arthur.marchetti.interfaces.TaxCalculator.ReceiptDBRepository;
import com.arthur.marchetti.services.ReceiptService;

import java.math.BigDecimal;

public class ReceiptDbSaver {
    private final ReceiptService service;
    private final ReceiptDBRepository db;
    public ReceiptDbSaver(ReceiptService service, ReceiptDBRepository db){ this.service = service; this.db = db; }

    public long renderAndSave(Cart cart, String customerName) throws Exception {
        var lines = service.render(cart);
        BigDecimal sub = cart.subtotal();
        var last4 = lines.subList(lines.size() - 3, lines.size());
        String subtotalText = last4.get(0).replaceAll("[^0-9.]+", "");
        String taxText = last4.get(1).replaceAll("[^0-9.]+", "");
        String totalText = last4.get(2).replaceAll("[^0-9.]+", "");
        return db.save(lines, customerName == null ? "Guest" : customerName, subtotalText, taxText, totalText);
    }
}