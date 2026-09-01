package org.example.cache.storage;

import java.util.HashMap;
import java.util.Map;

public class MapStorage<K, V> implements Storage<K, V> {
    private final Map<K, V> storageMap;

    public MapStorage(){
        storageMap = new HashMap<>();
    }

    @Override
    public void add(K key, V value){
        storageMap.put(key, value);
    }

    @Override
    public V get(K key){
        if(!contains(key)){
            return null;
        }
        return storageMap.get(key);
    }

    @Override
    public boolean contains(K key){
        return storageMap.containsKey(key);
    }

    @Override
    public void remove(K key){
        storageMap.remove(key);
    }

    @Override
    public int size(){
        return storageMap.size();
    }
}
