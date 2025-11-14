package com.arthur.marchetti.model;

import com.arthur.marchetti.interfaces.TaxCalculator.ReceiptRepository;
import com.arthur.marchetti.services.ReceiptService;

import java.nio.file.Path;

public class ReceiptSaver {
    private final ReceiptService service;
    private final ReceiptRepository repo;
    public ReceiptSaver(ReceiptService service, ReceiptRepository repo){
        this.service = service; this.repo = repo;
    }
    public Path renderAndSave(Cart cart) throws Exception {
        var lines = service.render(cart);
        return repo.save(lines);
    }
}