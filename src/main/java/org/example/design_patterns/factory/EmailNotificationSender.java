package org.example.design_patterns.factory;

public class EmailNotificationSender implements NotificationSender{
    @Override
    public void send(NotificationRequest request){
        System.out.println("Email Sent to the recepient: "+request.getRecepient());
    }
}
