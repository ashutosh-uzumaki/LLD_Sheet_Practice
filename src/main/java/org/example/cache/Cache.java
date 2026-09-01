package org.example.cache;

import org.example.cache.eviction_policies.EvictionPolicy;
import org.example.cache.storage.Storage;

public class Cache<K, V> {

    private final Storage<K, V> storage;
    private final EvictionPolicy<K> evictionPolicy;
    private final int maxCapacity = 3;

    public Cache(Storage<K, V> storage,
                 EvictionPolicy<K> evictionPolicy) {
        this.storage = storage;
        this.evictionPolicy = evictionPolicy;
    }

    public void put(K key, V value) {

        if (storage.contains(key)) {
            storage.add(key, value);
            evictionPolicy.markAccessed(key);
        } else {

            if (storage.size() >= maxCapacity) {
                K evictedKey = evictionPolicy.evict();
                storage.remove(evictedKey);
            }

            storage.add(key, value);
            evictionPolicy.addNewNode(key);
        }
    }

    public V get(K key) {
        if (!storage.contains(key)) {
            return null;
        }

        evictionPolicy.markAccessed(key);
        return storage.get(key);
    }
}