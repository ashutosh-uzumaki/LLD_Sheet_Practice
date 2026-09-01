package org.example.cache.lru;

public class DoublyLinkedList<K> {
    private Node<K> head;
    private Node<K> tail;

    public DoublyLinkedList(){
        head = null;
        tail = null;
    }

    public void add(Node<K> node){
        if(head == null){
            head = node;
            tail = node;
        }else{
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    public K evict() {
        if (head == null) {
            return null;
        }

        Node<K> evictionNode = head;
        head = head.next;

        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }

        return evictionNode.getKey();
    }

    public void moveToMostRecentlyUsed(Node<K> node){
        if(node == tail){
            return;
        }

        if(node == head){
            head = head.next;
            head.prev = null;
        }else if (node.prev != null && node.next != null){
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        tail.next = node;
        node.prev = tail;
        node.next = null;
        tail = node;
    }
}
