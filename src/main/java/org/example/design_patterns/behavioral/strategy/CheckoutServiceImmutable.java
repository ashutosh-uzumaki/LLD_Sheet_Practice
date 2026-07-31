package org.example.design_patterns.behavioral.strategy;

import java.math.BigDecimal;
import java.util.Objects;

public class CheckoutServiceImmutable {
    private final Payment payment;

    public CheckoutServiceImmutable(Payment payment){
        this.payment = Objects.requireNonNull(payment);
    }

    public void checkout(BigDecimal amount){
        Objects.requireNonNull(amount);
        payment.pay(amount);
    }
}
