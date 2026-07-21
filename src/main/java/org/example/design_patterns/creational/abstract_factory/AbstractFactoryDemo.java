package org.example.design_patterns.creational.abstract_factory;

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        PlatformFactory factory = new PlatformAFactory();
        LoginScreen loginScreen = new LoginScreen(factory);
        loginScreen.showLoginScreen();
    }
}
