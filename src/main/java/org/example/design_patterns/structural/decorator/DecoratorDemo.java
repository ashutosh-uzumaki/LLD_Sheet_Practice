package org.example.design_patterns.structural.decorator;

public class DecoratorDemo {
    public static void main(String[] args) {
        Pizza pizza = new SimplePizza();
        pizza = new CheesePizza(pizza);
        pizza = new MushroomPizza(pizza);
        System.out.println(pizza.price());
    }
}
