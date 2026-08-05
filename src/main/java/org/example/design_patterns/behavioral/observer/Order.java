package org.example.design_patterns.behavioral.observer;

import java.math.BigDecimal;
import java.util.*;

public class Order {
    private final String orderId;
    private final List<String> productIds;
    private final BigDecimal amount;
    private final List<OrderObserver> observers;


    public Order(String orderId, List<String> productIds, BigDecimal amount){
        this.orderId = Objects.requireNonNull(orderId, "orderId cannot be null");
        this.productIds = new ArrayList<>(Objects.requireNonNull(productIds, "productIds cannot be null"));
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        observers = new ArrayList<>();
    }

    public void registerObserver(OrderObserver observer){
        observers.add(Objects.requireNonNull(observer));
    }

    public void unregisterObserver(OrderObserver observer){
        observers.remove(Objects.requireNonNull(observer));
    }

    public void placeOrder(){
        OrderPlaceEvent placeEvent = new OrderPlaceEvent(this.orderId, this.productIds, this.amount);
        System.out.println("Order has been Placed");
        onOrderPlaced(placeEvent);
    }

    private void onOrderPlaced(OrderPlaceEvent orderPlaceEvent){
        for(OrderObserver observer: observers){
            observer.onOrderPlaced(orderPlaceEvent);
        }
    }
}
