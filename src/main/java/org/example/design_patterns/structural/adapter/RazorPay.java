package org.example.design_patterns.structural.adapter;

import java.math.BigDecimal;

public class RazorPay {
    public String charge(BigDecimal amount, String userId){
        return "RazorPay charged: "+amount+" for the user: "+userId;
    }
}
