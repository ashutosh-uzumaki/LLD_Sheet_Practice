package org.example.design_patterns.behavioral.observer;

public class EmailService implements OrderObserver{
    @Override
    public void onOrderPlaced(OrderPlaceEvent event){
        System.out.println("Sending Email for Order Place event: "+event.toString());
    }
}
