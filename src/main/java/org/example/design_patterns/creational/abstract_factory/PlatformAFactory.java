package org.example.design_patterns.creational.abstract_factory;

public class PlatformAFactory implements PlatformFactory{
    @Override
    public Button createButton(){
        return new PlatformAButton();
    }

    @Override
    public TextBox createTextBox(){
        return new PlatformATextBox();
    }
}
