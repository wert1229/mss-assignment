package com.musinsa.assignment.product.application.listener;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toList;

import com.musinsa.assignment.product.application.contract.CacheManager;
import com.musinsa.assignment.product.application.contract.ProductRepository;
import com.musinsa.assignment.product.application.exception.ProductNotFoundException;
import com.musinsa.assignment.product.application.listener.event.AddBrandEvent;
import com.musinsa.assignment.product.application.listener.event.AddProductEvent;
import com.musinsa.assignment.product.application.listener.event.RemoveProductEvent;
import com.musinsa.assignment.product.application.listener.event.UpdateProductEvent;
import com.musinsa.assignment.product.domain.Product;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventListener {
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;

    @EventListener
    public void listen(AddProductEvent event) {
        updateOneProduct(event.productId());
    }

    @EventListener
    public void listen(UpdateProductEvent event) {
        updateOneProduct(event.productId());
    }

    @EventListener
    public void listen(RemoveProductEvent event) {
        updateOneProduct(event.productId());
    }

    private void updateOneProduct(Long productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);

        updateCategoryMinPrice(product);
        updateCategoryMaxPrice(product);
        updateBrandMinPrice(productRepository.findAllByBrandId(product.getBrandId()));
    }

    @EventListener
    public void listen(AddBrandEvent event) {
        var brandProducts = productRepository.findAllByBrandId(event.brandId());
        brandProducts.forEach(newProduct -> {
            updateCategoryMinPrice(newProduct);
            updateCategoryMaxPrice(newProduct);
        });

        updateBrandMinPrice(brandProducts);
    }

    private void updateCategoryMinPrice(Product newProduct) {
        var key = "CATEGORY:MIN:" + newProduct.getCategory().name();

        var oldProduct = cacheManager.get(key, Product.class);
        oldProduct.ifPresentOrElse(
            product -> {
                if (product.getPrice() > newProduct.getPrice()) {
                    cacheManager.set(key, newProduct);
                }
            },
            () -> cacheManager.set(key, newProduct)
        );
    }

    private void updateCategoryMaxPrice(Product newProduct) {
        var key = "CATEGORY:MAX:" + newProduct.getCategory().name();

        var oldProduct = cacheManager.get(key, Product.class);
        oldProduct.ifPresentOrElse(
            product -> {
                if (product.getPrice() <= newProduct.getPrice()) {
                    cacheManager.set(key, newProduct);
                }
            },
            () -> cacheManager.set(key, newProduct)
        );
    }

    private void updateBrandMinPrice(List<Product> newBrandProducts) {
        var key = "BRAND:MIN";

        var minBrandProducts = newBrandProducts.stream()
            .collect(groupingBy(
                Product::getCategory,
                minBy(Comparator.comparingInt(Product::getPrice))
            ))
            .values().stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());

        var minTotalPrice = minBrandProducts.stream()
            .mapToInt(Product::getPrice)
            .sum();

        var newBrandCacheData = new BrandMinCacheData(
            minBrandProducts,
            minTotalPrice
        );

        var oldBrand = cacheManager.get(key, BrandMinCacheData.class);
        oldBrand.ifPresentOrElse(
            brand -> {
                if (brand.totalPrice() > newBrandCacheData.totalPrice()) {
                    cacheManager.set(key, newBrandCacheData);
                }
            },
            () -> cacheManager.set(key, newBrandCacheData)
        );
    }

    public record BrandMinCacheData(
        List<Product> products,
        Integer totalPrice
    ) {
    }
}
