package com.arthur.marchetti.interfaces.TaxCalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ReceiptDBRepository {
    long save(List<String> lines, String customerName, String subtotal, String tax, String total) throws Exception;
}