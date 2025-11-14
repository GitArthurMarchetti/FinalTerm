package com.arthur.marchetti;


import com.arthur.marchetti.model.*;
import com.arthur.marchetti.repo.SqliteReceiptRepository;
import com.arthur.marchetti.services.ReceiptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SqliteReceiptRepositoryTest {
    @TempDir
    Path temp;

    @Test
    void saves_receipt_and_returns_id() throws Exception {
        // Arrange
        Cart cart = new Cart();
        cart.add(new MenuItem("Americano", new BigDecimal("3.50"), Category.DRINK), 2);
        ReceiptService svc = new ReceiptService(new FlatRateTaxCalculator(new BigDecimal("0.06")));


//        var repo = new SqliteReceiptRepository(temp.resolve("receipts.db"));
        
        var repo = new SqliteReceiptRepository(Path.of("/home/arthur/Arthur/College/rp.db"));

        var saver = new ReceiptDbSaver(svc, repo);

        // Act
        long id = saver.renderAndSave(cart, "Alice");

        // Assert
        assertTrue(id > 0);
    }
}