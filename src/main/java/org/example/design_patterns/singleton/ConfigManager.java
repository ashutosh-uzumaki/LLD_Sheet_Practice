package org.example.design_patterns.singleton;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private final String tenantId;
    private final static ConcurrentHashMap<String, ConfigManager> tenantConfigMap = new ConcurrentHashMap<>();

    private ConfigManager(String tenantId){
        this.tenantId = tenantId;
    }

    public static ConfigManager getInstance(String tenantId){
        return tenantConfigMap.computeIfAbsent(tenantId, k-> new ConfigManager(tenantId));
    }
}
