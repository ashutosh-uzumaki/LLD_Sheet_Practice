package org.example.cache;

import org.example.cache.eviction_policies.EvictionPolicy;
import org.example.cache.eviction_policies.LruEvictionPolicy;
import org.example.cache.storage.MapStorage;
import org.example.cache.storage.Storage;

public class Demo {

    public static void main(String[] args) {

        Storage<String, Integer> storage = new MapStorage<>();
        EvictionPolicy<String> evictionPolicy = new LruEvictionPolicy<>();

        Cache<String, Integer> cache =
                new Cache<>(storage, evictionPolicy);

        // Scenario 1: Add new keys
        cache.put("A", 10);
        cache.put("B", 20);
        cache.put("C", 30);

        System.out.println("A = " + cache.get("A"));
        System.out.println("B = " + cache.get("B"));
        System.out.println("C = " + cache.get("C"));

        // Scenario 2: Access A
        // Expected recency:
        // B -> C -> A

        cache.get("A");

        // Scenario 3: Update existing key
        cache.put("A", 100);

        System.out.println("Updated A = " + cache.get("A"));

        // Scenario 4: Access B
        // Expected recency:
        // C -> A -> B

        cache.get("B");

        // Scenario 5: Add a new key when capacity is full
        //
        // For this test, temporarily make maxCapacity = 3
        //
        // Current recency:
        // C -> A -> B
        //
        // Adding D should evict C.

        cache.put("D", 40);

        System.out.println("C after eviction = " + cache.get("C"));
        System.out.println("A = " + cache.get("A"));
        System.out.println("B = " + cache.get("B"));
        System.out.println("D = " + cache.get("D"));
    }
}