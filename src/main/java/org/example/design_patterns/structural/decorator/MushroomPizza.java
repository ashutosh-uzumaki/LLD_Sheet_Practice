package org.example.design_patterns.structural.decorator;

public class MushroomPizza implements Pizza{
    private final int cost = 30;
    private Pizza pizza;

    public MushroomPizza(Pizza pizza){
        this.pizza = pizza;
    }
    @Override
    public int price(){
        return pizza.price()+cost;
    }
}
