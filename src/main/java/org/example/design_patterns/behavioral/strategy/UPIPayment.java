package org.example.design_patterns.behavioral.strategy;

import java.math.BigDecimal;

public class UPIPayment implements Payment{
    @Override
    public void pay(BigDecimal amount){
        System.out.println("UPI Payment of amount: "+amount.toString()+" completed.");
    }
}
