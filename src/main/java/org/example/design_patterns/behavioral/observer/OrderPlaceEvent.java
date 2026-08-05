package org.example.design_patterns.behavioral.observer;

import java.math.BigDecimal;
import java.util.*;

public class OrderPlaceEvent {
    private final String orderId;
    private final List<String> productIds;
    private final BigDecimal amount;

    public OrderPlaceEvent(String orderId, List<String> productIds, BigDecimal amount){
        this.orderId = Objects.requireNonNull(orderId, "orderId cannot be null");
        this.productIds = new ArrayList<>(Objects.requireNonNull(productIds, "productIds cannot be null"));
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
    }

    public String getOrderId() {
        return orderId;
    }

    public List<String> getProductIds() {
        return Collections.unmodifiableList(productIds);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "OrderPlaceEvent{" +
                "orderId='" + orderId + '\'' +
                ", productIds=" + productIds +
                ", amount=" + amount +
                '}';
    }
}
