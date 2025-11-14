package com.arthur.marchetti;


import com.arthur.marchetti.model.*;
import com.arthur.marchetti.repo.FileReceiptRepository;
import com.arthur.marchetti.services.ReceiptService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReceiptFileTest {
    @TempDir
    Path temp;

    @Test void writes_receipt_file() throws Exception {
        var cart = new Cart();
        cart.add(new MenuItem("Coffee", new BigDecimal("3.00"), Category.DRINK), 2);

        var tax = new FlatRateTaxCalculator(new BigDecimal(0.06));


        var svc = new ReceiptService(tax);

        var lines = svc.render(cart);

        var printer = new FileReceiptRepository(temp);

        Path p = printer.save(lines);

        assertTrue(Files.exists(p));
        assertTrue(Files.size(p) > 0);


        var r = new FileReader(p.toFile());

        var br = new BufferedReader(r);

        String line = br.readLine();
        int counter = 0;
        while (line != null){
            assertEquals(lines.get(counter), line);
            line = br.readLine();
            counter++;
        }

        new FileReader(p.toFile());
    }

}
