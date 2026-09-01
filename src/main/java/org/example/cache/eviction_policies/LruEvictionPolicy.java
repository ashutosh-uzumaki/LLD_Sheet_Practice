package org.example.cache.eviction_policies;

import org.example.cache.lru.DoublyLinkedList;
import org.example.cache.lru.Node;

import java.util.HashMap;
import java.util.Map;

public class LruEvictionPolicy<K> implements EvictionPolicy<K>{
    private final Map<K, Node<K>> nodeMap;
    private final DoublyLinkedList<K> list;
    public LruEvictionPolicy(){
        list = new DoublyLinkedList<>();
        nodeMap = new HashMap<>();
    }

    @Override
    public K evict() {
        K key = list.evict();
        if(key != null){
            nodeMap.remove(key);
        }
        return key;
    }

    @Override
    public void addNewNode(K key){
        Node<K> node = new Node<>(key);
        list.add(node);
        nodeMap.put(node.getKey(), node);
    }

    @Override
    public void markAccessed(K key) {
        if(!nodeMap.containsKey(key)){
            return;
        }
        Node<K> node = nodeMap.get(key);
        list.moveToMostRecentlyUsed(node);
    }
}
