package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        Approver manager = new Manager();
        Approver seniorManager = new SeniorManager();
        manager.setNext(seniorManager);
        Approver director = new Director();
        seniorManager.setNext(director);
        ExpenseRequest expenseRequest = new ExpenseRequest("1", new BigDecimal(85000));
        manager.process(expenseRequest);
    }
}
