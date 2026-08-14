package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class Director extends Approver{
    public Director(){
        super(new BigDecimal("100000"));
    }

    @Override
    protected void approve(ExpenseRequest expenseRequest){
        System.out.println("Director approve the expense: "+expenseRequest.getExpenseId());
    }
}
