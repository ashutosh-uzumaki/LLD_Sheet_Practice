package org.example.design_patterns.structural.decorator;

public class CheesePizza implements Pizza{
    private final int cost = 230;
    private Pizza pizza;
    public CheesePizza(Pizza pizza){
        this.pizza = pizza;
    }
    public int price(){
        return pizza.price() + cost;
    }
}
