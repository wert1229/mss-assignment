package com.musinsa.assignment.product.application.contract;

import java.util.Optional;

public interface CacheManager {

    <T> void set(String key, T data);

    <T> Optional<T> get(String key, Class<T> clazz);
}
