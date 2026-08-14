package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class SeniorManager extends Approver{
    public SeniorManager(){
        super(new BigDecimal("50000"));
    }

    @Override
    protected void approve(ExpenseRequest expenseRequest){
        System.out.println("Senior Manager approved the request "+expenseRequest.getAmount());
    }
}
