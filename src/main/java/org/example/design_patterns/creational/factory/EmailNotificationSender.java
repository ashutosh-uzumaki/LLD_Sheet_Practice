package org.example.design_patterns.creational.factory;

public class EmailNotificationSender implements NotificationSender{
    @Override
    public void send(NotificationRequest request){
        System.out.println("Email Sent to the recepient: "+request.getRecepient());
    }
}
