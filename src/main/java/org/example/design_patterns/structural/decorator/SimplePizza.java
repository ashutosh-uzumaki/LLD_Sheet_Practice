package org.example.design_patterns.structural.decorator;

public class SimplePizza implements Pizza{
    private final int price = 130;

    @Override
    public int price(){
        return price;
    }
}
