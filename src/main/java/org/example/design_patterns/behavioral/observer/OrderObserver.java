package org.example.design_patterns.behavioral.observer;

public interface OrderObserver {
    void onOrderPlaced(OrderPlaceEvent orderPlaceEvent);
}
