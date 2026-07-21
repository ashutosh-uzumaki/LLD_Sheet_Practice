package org.example.design_patterns.creational.abstract_factory;

public class PlatformATextBox implements TextBox{
    @Override
    public void render(){
        System.out.println("Rendering Platform A text box");
    }

    @Override
    public void setText(String input){
        System.out.println("Input is set to: "+input);
    }
}
