package org.example.cache.storage;

public interface Storage<K, V> {
    void add(K key, V value);
    V get(K key);
    void remove(K key);
    boolean contains(K key);
    int size();
}
