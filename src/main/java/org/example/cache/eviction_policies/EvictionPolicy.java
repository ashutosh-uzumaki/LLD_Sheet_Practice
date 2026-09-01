package org.example.cache.eviction_policies;

import org.example.cache.lru.Node;

public interface EvictionPolicy<K> {
    void addNewNode(K key);
    void markAccessed(K key);
    K evict();
}
