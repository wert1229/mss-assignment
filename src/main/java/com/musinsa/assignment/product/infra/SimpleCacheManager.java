package com.musinsa.assignment.product.infra;

import com.musinsa.assignment.product.application.contract.CacheManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SimpleCacheManager implements CacheManager {
    private final Map<String, Object> cache;

    public SimpleCacheManager() {
        this.cache = new HashMap<>();
    }

    @Override
    public <T> void set(String key, T data) {
        cache.put(key, data);
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        var data = cache.get(key);
        return data == null ? Optional.empty() : Optional.of(clazz.cast(data));
    }
}
