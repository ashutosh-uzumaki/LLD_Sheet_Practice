package org.example.design_patterns.behavioral.observer;

public class InventoryService implements OrderObserver{
    @Override
    public void onOrderPlaced(OrderPlaceEvent event){
        System.out.println("Reducing inventory for the event: "+event.toString());
    }
}
