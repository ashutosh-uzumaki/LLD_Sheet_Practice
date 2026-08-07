package org.example.design_patterns.structural.adapter;

import java.math.BigDecimal;

public interface PaymentGateway {
    boolean processPayment(String userId, BigDecimal amount, String currency);
}
