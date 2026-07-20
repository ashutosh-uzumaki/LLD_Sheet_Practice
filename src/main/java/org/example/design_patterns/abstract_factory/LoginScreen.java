package org.example.design_patterns.abstract_factory;

public class LoginScreen {
    private final PlatformFactory factory;
    public LoginScreen(PlatformFactory factory){
        this.factory = factory;
    }

    public void showLoginScreen(){
        factory.createButton().render();
        factory.createTextBox().render();
    }
}
