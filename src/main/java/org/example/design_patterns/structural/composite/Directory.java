package org.example.design_patterns.structural.composite;

import java.util.*;

public class Directory implements StorageItem{
    private final List<StorageItem> items;

    public Directory(){
        items = new ArrayList<>();
    }

    public void add(StorageItem item){
        items.add(item);
    }

    public void remove(StorageItem item){
        items.remove(item);
    }

    @Override
    public int getSize(){
        int size = 0;
        for(StorageItem item: items){
            size += item.getSize();
        }
        return size;
    }
}
