package org.example.design_patterns.structural.composite;

public class File implements StorageItem{
    private final int size;
    public File(int size){
        this.size = size;
    }

    @Override
    public int getSize(){
        return size;
    }
}
