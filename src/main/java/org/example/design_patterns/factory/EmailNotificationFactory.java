package org.example.design_patterns.factory;

public class EmailNotificationFactory implements NotificationFactory{
    @Override
    public NotificationSender createSender(){
        return new EmailNotificationSender();
    }
}
