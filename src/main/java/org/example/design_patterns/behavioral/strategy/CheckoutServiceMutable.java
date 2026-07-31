package org.example.design_patterns.behavioral.strategy;

import java.math.BigDecimal;
import java.util.Objects;

public class CheckoutServiceMutable {
    private Payment payment;

    public CheckoutServiceMutable(Payment payment){
        this.payment = Objects.requireNonNull(payment);
    }

    public void setPayment(Payment payment){
        this.payment = Objects.requireNonNull(payment);
    }

    public void checkout(BigDecimal amount){
        payment.pay(amount);
    }
}
