package com.arthur.marchetti.model;

import com.arthur.marchetti.interfaces.TaxCalculator.TaxCalculator;

import java.math.BigDecimal;

public class FlatRateTaxCalculator implements TaxCalculator {
    private final BigDecimal rate; // for example 0.06 means 6 percent
    public FlatRateTaxCalculator(BigDecimal rate){
        if(rate == null || rate.signum() < 0) throw new IllegalArgumentException("rate must be non negative");
        this.rate = rate;
    }
    @Override public BigDecimal tax(BigDecimal amount){
        if(amount == null || amount.signum() < 0) throw new IllegalArgumentException("amount must be non negative");
        return amount.multiply(rate).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}