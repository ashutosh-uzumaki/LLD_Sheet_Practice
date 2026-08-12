package org.example.design_patterns.structural.decorator;

public class CheesePizza extends BaseDecorator{
    private final int cost = 230;
    public CheesePizza(Pizza pizza){
        super(pizza);
    }
    public int price(){
        return super.price() + cost;
    }
}
