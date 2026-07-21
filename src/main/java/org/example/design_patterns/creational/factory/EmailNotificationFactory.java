package org.example.design_patterns.creational.factory;

public class EmailNotificationFactory implements NotificationFactory{
    @Override
    public NotificationSender createSender(){
        return new EmailNotificationSender();
    }
}
