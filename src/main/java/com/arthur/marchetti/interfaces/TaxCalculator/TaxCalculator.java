package com.arthur.marchetti.interfaces.TaxCalculator;

import java.math.BigDecimal;

public interface TaxCalculator {
    BigDecimal tax(BigDecimal amount);
}