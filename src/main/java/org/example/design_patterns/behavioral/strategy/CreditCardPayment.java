package org.example.design_patterns.behavioral.strategy;

import java.math.BigDecimal;

public class CreditCardPayment implements Payment{
    @Override
    public void pay(BigDecimal amount){
        System.out.println("Credit card payment of amount: "+amount+" completed.");
    }
}
