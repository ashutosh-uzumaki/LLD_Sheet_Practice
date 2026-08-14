package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

class Manager extends Approver {

    public Manager() {
        super(new BigDecimal("10000"));
    }

    @Override
    protected void approve(ExpenseRequest request) {
        System.out.println("Manager approved " + request.getExpenseId());
    }
}
