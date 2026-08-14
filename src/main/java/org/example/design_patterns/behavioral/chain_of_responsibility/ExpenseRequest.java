package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class ExpenseRequest {
    private final String expenseId;
    private final BigDecimal amount;

    public ExpenseRequest(String expenseId, BigDecimal amount){
        this.expenseId = expenseId;
        this.amount = amount;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
