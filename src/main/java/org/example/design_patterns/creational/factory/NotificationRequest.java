package org.example.design_patterns.creational.factory;

public class NotificationRequest {
    private final String sender;
    private final String recepient;
    private final String message;

    NotificationRequest(String sender, String recepient, String message){
        this.sender = sender;
        this.recepient = recepient;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getRecepient() {
        return recepient;
    }

    public String getMessage() {
        return message;
    }
}
