package org.example.design_patterns.abstract_factory;

public class PlatformAButton implements Button{
    @Override
    public void render(){
        System.out.println("Rendering Platform A button");
    }
}
