package org.example.design_patterns.behavioral.observer;

import java.math.BigDecimal;
import java.util.List;

public class ObserverDemo {
    public static void main(String[] args) {
        Order order = new Order("12", List.of("1", "2", "3"), new BigDecimal(100));
        EmailService emailService = new EmailService();
        InventoryService inventoryService = new InventoryService();
        order.registerObserver(emailService);
        order.registerObserver(inventoryService);
        order.placeOrder();
    }
}
