package com.musinsa.assignment.product.application;

import com.musinsa.assignment.product.application.contract.BrandRepository;
import com.musinsa.assignment.product.application.contract.ProductRepository;
import com.musinsa.assignment.product.application.dto.AddBrandDto;
import com.musinsa.assignment.product.application.dto.AddProductDto;
import com.musinsa.assignment.product.application.dto.UpdateProductDto;
import com.musinsa.assignment.product.application.exception.BrandNotFoundException;
import com.musinsa.assignment.product.application.exception.CategoryEmptyException;
import com.musinsa.assignment.product.application.exception.ProductNotFoundException;
import com.musinsa.assignment.product.application.listener.event.AddBrandEvent;
import com.musinsa.assignment.product.application.listener.event.AddProductEvent;
import com.musinsa.assignment.product.application.listener.event.RemoveProductEvent;
import com.musinsa.assignment.product.application.listener.event.UpdateProductEvent;
import com.musinsa.assignment.product.domain.Brand;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void addProduct(AddProductDto dto) {
        checkIfBrandExist(dto.brandId());

        var newProduct = new Product(
            dto.brandId(),
            dto.category(),
            dto.price()
        );

        var newProductId = productRepository.save(newProduct);

        eventPublisher.publishEvent(
            new AddProductEvent(newProductId)
        );
    }

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
            new UpdateProductEvent(product.getId())
        );
    }

    public void removeProduct(Long productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);

        checkIfCategoryEmpty(product.getBrandId(), product.getCategory());

        productRepository.delete(product.getId());

        eventPublisher.publishEvent(
            new RemoveProductEvent(product.getId())
        );
    }

    public void addBrand(AddBrandDto dto) {
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
            new AddBrandEvent(newBrandId)
        );
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
