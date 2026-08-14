package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public abstract class Approver {
    private Approver next;
    private final BigDecimal limit;
    public Approver(BigDecimal limit){
        this.limit = limit;
    }
    public void setNext(Approver approver){
        next = approver;
    }

    public void forward(ExpenseRequest request){
        if(next == null){
            throw new RuntimeException("Request cannot be approved. Check the limits!");
        }
        next.process(request);
    }

    protected boolean canProcess(BigDecimal amount){
        return amount.compareTo(limit) <= 0;
    }

    public void process(ExpenseRequest request){
        if (canProcess(request.getAmount())) {
            approve(request);
        }else{
            forward(request);
        }
    }

    protected abstract void approve(ExpenseRequest request);
}
