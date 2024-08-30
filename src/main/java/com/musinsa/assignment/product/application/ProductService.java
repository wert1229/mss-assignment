package com.musinsa.assignment.product.application;

import com.musinsa.assignment.product.application.contract.BrandRepository;
import com.musinsa.assignment.product.application.contract.ProductRepository;
import com.musinsa.assignment.product.application.dto.AddBrandDto;
import com.musinsa.assignment.product.application.dto.AddProductDto;
import com.musinsa.assignment.product.application.dto.UpdateProductDto;
import com.musinsa.assignment.product.application.exception.BrandNotFoundException;
import com.musinsa.assignment.product.application.exception.CategoryEmptyException;
import com.musinsa.assignment.product.application.exception.ProductNotFoundException;
import com.musinsa.assignment.product.application.listener.event.ProductChangeEvent;
import com.musinsa.assignment.product.domain.Brand;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long addProduct(AddProductDto dto) {
        checkIfBrandExist(dto.brandId());

        var newProduct = new Product(
            dto.brandId(),
            dto.category(),
            dto.price()
        );

        var newId = productRepository.save(newProduct);

        eventPublisher.publishEvent(
            new ProductChangeEvent()
        );

        return newId;
    }

    @Transactional
    public void updateProduct(Long productId, UpdateProductDto dto) {
        var product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);

        checkIfBrandExist(dto.brandId());
        checkIfCategoryEmpty(product.getBrandId(), product.getCategory());

        product.update(
            dto.brandId(),
            dto.category(),
            dto.price()
        );

        productRepository.save(product);

        eventPublisher.publishEvent(
            new ProductChangeEvent()
        );
    }

    @Transactional
    public void removeProduct(Long productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);

        checkIfCategoryEmpty(product.getBrandId(), product.getCategory());

        productRepository.delete(product);

        eventPublisher.publishEvent(
            new ProductChangeEvent()
        );
    }

    @Transactional
    public Long addBrand(AddBrandDto dto) {
        var newBrandId = brandRepository.save(new Brand(dto.brandName()));
        dto.products().forEach(product ->
            productRepository.save(
                new Product(
                    newBrandId,
                    product.category(),
                    product.price()
                )
            )
        );

        eventPublisher.publishEvent(
            new ProductChangeEvent()
        );

        return newBrandId;
    }

    private void checkIfBrandExist(Long brandId) {
        var brand = brandRepository.findById(brandId);
        if (brand.isEmpty()) {
            throw new BrandNotFoundException();
        }
    }

    private void checkIfCategoryEmpty(Long brandId, Category category) {
        var count = productRepository.countByBrandAndCategory(brandId, category);
        if (count <= 1) {
            throw new CategoryEmptyException();
        }
    }
}
