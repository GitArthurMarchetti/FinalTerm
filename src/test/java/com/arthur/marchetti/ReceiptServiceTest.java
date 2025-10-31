package com.arthur.marchetti;

import com.arthur.marchetti.model.Cart;
import com.arthur.marchetti.model.FlatRateTaxCalculator;
import com.arthur.marchetti.repo.InMemoryCatalogRepository;
import com.arthur.marchetti.services.ReceiptService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReceiptServiceTest {
    @Test
    void totals_and_tax(){
        var repo = new InMemoryCatalogRepository();
        var cart = new Cart();
        var cartItem =repo.all().get(0);

        cart.add(cartItem, 2); // coffee

        var tax = new FlatRateTaxCalculator(new BigDecimal("0.06"));

        var svc = new ReceiptService(tax);
        var lines = svc.render(cart);

        var lastLine = lines.get(lines.size() - 1);

        assertTrue(lastLine.contains("Total:cu"));
    }
}
