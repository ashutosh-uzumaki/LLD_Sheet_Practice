package org.example.cache.lru;

public class Node<K> {
    private final K key;
    Node<K> next;
    Node<K> prev;
    public Node(K key){
        this.key = key;
    }
    public K getKey(){
        return key;
    }
}
