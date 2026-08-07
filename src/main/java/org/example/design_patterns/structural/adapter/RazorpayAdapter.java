package org.example.design_patterns.structural.adapter;

import java.math.BigDecimal;

public class RazorpayAdapter implements PaymentGateway{
    private final RazorPay razorPay;
    public RazorpayAdapter(RazorPay razorPay){
        this.razorPay = razorPay;
    }
    @Override
    public boolean processPayment(String userId, BigDecimal amount, String currency){
        String result = razorPay.charge(amount, userId);
        return result != null && !result.isEmpty();
    }
}
