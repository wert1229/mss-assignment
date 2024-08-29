package com.musinsa.assignment.product.application.listener;

import static java.util.stream.Collectors.toMap;

import com.musinsa.assignment.product.application.contract.CacheManager;
import com.musinsa.assignment.product.application.contract.ProductRepository;
import com.musinsa.assignment.product.application.listener.event.ProductChangeEvent;
import com.musinsa.assignment.product.domain.Product;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventListener {
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;

    @EventListener
    public void listen(ProductChangeEvent event) {
        updateCategoryMinPrice();
        updateCategoryMaxPrice();
        updateBrandMinPrice();
    }

    private void updateCategoryMinPrice() {
        var minProductMap = productRepository.findMinPriceProductsByCategory().stream()
            .collect(toMap(
                Product::getCategory,
                Function.identity()
            ));

        minProductMap.keySet()
            .forEach(category -> {
                var key = "CATEGORY:MIN:" + category.name();
                var minProduct = minProductMap.get(category);
               cacheManager.set(key, minProduct);
            });
    }

    private void updateCategoryMaxPrice() {
        var maxProductMap = productRepository.findMaxPriceProductsByCategory().stream()
            .collect(toMap(
                Product::getCategory,
                Function.identity()
            ));

        maxProductMap.keySet()
            .forEach(category -> {
                var key = "CATEGORY:MAX:" + category.name();
                var maxProduct = maxProductMap.get(category);
                cacheManager.set(key, maxProduct);
            });
    }

    private void updateBrandMinPrice() {
        var key = "BRAND:MIN";
        var products = productRepository.findAllByBrandMinPrice();

        if (!products.isEmpty()) {
            cacheManager.set(key, new BrandMinCacheData(products));
        }
    }

    public record BrandMinCacheData(
        List<Product> products
    ) {
    }
}
