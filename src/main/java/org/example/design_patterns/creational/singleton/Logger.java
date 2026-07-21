package org.example.design_patterns.creational.singleton;

public class Logger {
    private static volatile Logger instance;

    private Logger(){

    }

    public static Logger getInstance(){
        if(instance == null){
            synchronized (Logger.class){
                if(instance == null){
                    instance = new Logger();
                }
            }
        }
        return instance;
    }
}
