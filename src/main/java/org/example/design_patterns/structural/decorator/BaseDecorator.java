package org.example.design_patterns.structural.decorator;

public abstract class BaseDecorator implements Pizza{

    private Pizza pizza;
    public BaseDecorator(Pizza pizza){
        this.pizza = pizza;
    }

    @Override
    public int price(){
        return pizza.price();
    }
}
