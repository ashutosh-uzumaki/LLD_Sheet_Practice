package org.example.design_patterns.creational.builder;

public class Laptop {
    private final String cpu;
    private final String gpu;
    private final String ram;
    private final String storage;
    private final boolean backlitKeyboard;

    private Laptop(LaptopBuilder builder){
        this.cpu = builder.cpu;
        this.gpu = builder.gpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.backlitKeyboard = builder.backlitKeyboard;
    }

    public static class LaptopBuilder{
        private final String cpu;
        private final String gpu;
        private final String ram;
        private final String storage;
        private boolean backlitKeyboard;

        public LaptopBuilder(String cpu, String gpu, String storage, String ram){
            this.cpu = cpu;
            this.gpu = gpu;
            this.storage = storage;
            this.ram = ram;
        }

        public LaptopBuilder setBacklitKeyboard(boolean hasBacklitKeyBoard){
            this.backlitKeyboard = hasBacklitKeyBoard;
            return this;
        }

        public Laptop build(){
            return new Laptop(this);
        }
    }
}
