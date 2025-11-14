package com.arthur.marchetti.interfaces.TaxCalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ReceiptRepository {
     public Path save(
            List<String> receiptLines
    ) throws IOException;
}
