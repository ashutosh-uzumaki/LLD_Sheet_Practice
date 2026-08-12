package org.example.design_patterns.structural.decorator;

public class MushroomPizza extends BaseDecorator{
    private final int cost = 30;

    public MushroomPizza(Pizza pizza){
        super(pizza);
    }
    @Override
    public int price(){
        return super.price()+cost;
    }
}
