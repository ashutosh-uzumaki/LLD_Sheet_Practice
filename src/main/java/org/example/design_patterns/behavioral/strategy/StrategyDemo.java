package org.example.design_patterns.behavioral.strategy;

import java.math.BigDecimal;

public class StrategyDemo {
    public static void main(String[] args) {
        CheckoutServiceMutable checkoutServiceMutable = new CheckoutServiceMutable(new UPIPayment());
        checkoutServiceMutable.checkout(new BigDecimal(100));
    }
}
