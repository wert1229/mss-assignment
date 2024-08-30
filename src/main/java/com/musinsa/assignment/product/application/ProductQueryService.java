package com.musinsa.assignment.product.application;

import com.musinsa.assignment.product.application.contract.BrandRepository;
import com.musinsa.assignment.product.application.contract.CacheManager;
import com.musinsa.assignment.product.application.exception.BrandNotFoundException;
import com.musinsa.assignment.product.application.listener.ProductEventListener.BrandMinCacheData;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import com.musinsa.assignment.product.presentation.ProductController.BrandMinPricesResponse;
import com.musinsa.assignment.product.presentation.ProductController.CategoryMinMaxPricesResponse;
import com.musinsa.assignment.product.presentation.ProductController.CategoriesMinPricesResponse;
import com.musinsa.assignment.product.presentation.ProductController.PresentationProduct;
import com.musinsa.assignment.product.presentation.util.CategoryUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductQueryService {
    private final BrandRepository brandRepository;
    private final CacheManager cacheManager;

    public CategoriesMinPricesResponse getCategoriesMinPrices() {
        var products = Arrays.stream(Category.values())
            .map(category -> cacheManager.get("CATEGORY:MIN:" + category.name(), Product.class))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::convertToPresentationProduct)
            .collect(Collectors.toList());

        var totalPrice = products.stream()
            .mapToInt(PresentationProduct::price)
            .sum();

        return new CategoriesMinPricesResponse(
            totalPrice,
            products
        );
    }

    public BrandMinPricesResponse getBrandMinPrices() {
        var brandProducts = cacheManager.get("BRAND:MIN", BrandMinCacheData.class)
            .orElseGet(() -> new BrandMinCacheData(Collections.emptyList()))
            .products();

        if (brandProducts.isEmpty()) {
            return new BrandMinPricesResponse(
                0,
                Collections.emptyList(),
                ""
            );
        }

        var presentationProducts = brandProducts.stream()
            .map(this::convertToPresentationProduct)
            .collect(Collectors.toList());

        var totalPrice = presentationProducts.stream()
            .mapToInt(PresentationProduct::price)
            .sum();

        return new BrandMinPricesResponse(
            totalPrice,
            presentationProducts,
            presentationProducts.get(0).brand()
        );
    }

    public CategoryMinMaxPricesResponse getCategoryMinMaxPrices(Category category) {
        var minProduct = cacheManager.get("CATEGORY:MIN:" + category.name(), Product.class)
            .orElseThrow();
        var maxProduct = cacheManager.get("CATEGORY:MAX:" + category.name(), Product.class)
            .orElseThrow();

        var categoryString = CategoryUtils.convertFrom(category);

        return new CategoryMinMaxPricesResponse(
            categoryString,
            List.of(convertToPresentationProduct(minProduct)),
            List.of(convertToPresentationProduct(maxProduct))
        );
    }

    private PresentationProduct convertToPresentationProduct(Product product) {
        return new PresentationProduct(
            CategoryUtils.convertFrom(product.getCategory()),
            getBrandName(product.getBrandId()),
            product.getPrice()
        );
    }

    private String getBrandName(Long brandId) {
        return brandRepository.findById(brandId)
            .orElseThrow(BrandNotFoundException::new)
            .getName();
    }
}
