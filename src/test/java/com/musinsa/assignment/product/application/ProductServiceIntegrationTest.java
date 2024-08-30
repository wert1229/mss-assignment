package com.musinsa.assignment.product.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.musinsa.assignment.IntegrationTestContext;
import com.musinsa.assignment.product.application.dto.AddBrandDto;
import com.musinsa.assignment.product.application.dto.AddProductDto;
import com.musinsa.assignment.product.application.dto.UpdateProductDto;
import com.musinsa.assignment.product.application.listener.ProductEventListener.BrandMinCacheData;
import com.musinsa.assignment.product.domain.Brand;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductServiceIntegrationTest extends IntegrationTestContext {

    @Test
    @DisplayName("상품이 추가되고 카테고리별 가장 비싼 가격이 캐시에 반영된다")
    void addProduct_maxPrice() {
        // given
        var brandId = initBrandData("A", 10000);

        // when
        var id = productService.addProduct(
            new AddProductDto(
                brandId,
                Category.TOP,
                20000
            )
        );

        // then
        assertThat(productRepository.findById(id)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MAX:TOP", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MAX:TOP", Product.class).get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("상품이 추가되고 카테고리별 가장 싼 가격이 캐시에 반영된다")
    void addProduct_minPrice() {
        // given
        var brandId = initBrandData("A", 10000);

        // when
        var id = productService.addProduct(
            new AddProductDto(
                brandId,
                Category.SHOES,
                5000
            )
        );

        // then
        assertThat(productRepository.findById(id)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MIN:SHOES", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MIN:SHOES", Product.class).get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("상품 카테고리가 변경되고 카테고리별 가장 비싼 가격이 캐시에 반영된다")
    void updateProduct_maxPrice() {
        // given
        var brandId = initBrandData("A", 10000);
        var productId = productService.addProduct(
            new AddProductDto(
                brandId,
                Category.SHOES,
                20000
            )
        );

        // when
        productService.updateProduct(
            productId,
            new UpdateProductDto(
                brandId,
                Category.TOP,
                20000
            )
        );

        // then
        assertThat(productRepository.findById(productId)).isPresent();
        assertThat(productRepository.findById(productId).get().getCategory()).isEqualTo(Category.TOP);
        assertThat(cacheManager.get("CATEGORY:MAX:SHOES", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MAX:SHOES", Product.class).get().getId()).isNotEqualTo(productId);
        assertThat(cacheManager.get("CATEGORY:MAX:TOP", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MAX:TOP", Product.class).get().getId()).isEqualTo(productId);
    }

    @Test
    @DisplayName("상품 카테고리가 변경되고 카테고리별 가장 싼 가격이 캐시에 반영된다")
    void updateProduct_minPrice() {
        // given
        var brandId = initBrandData("A", 10000);
        var productId = productService.addProduct(
            new AddProductDto(
                brandId,
                Category.SHOES,
                5000
            )
        );

        // when
        productService.updateProduct(
            productId,
            new UpdateProductDto(
                brandId,
                Category.TOP,
                5000
            )
        );

        // then
        assertThat(productRepository.findById(productId)).isPresent();
        assertThat(productRepository.findById(productId).get().getCategory()).isEqualTo(Category.TOP);
        assertThat(cacheManager.get("CATEGORY:MIN:SHOES", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MIN:SHOES", Product.class).get().getId()).isNotEqualTo(productId);
        assertThat(cacheManager.get("CATEGORY:MIN:TOP", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MIN:TOP", Product.class).get().getId()).isEqualTo(productId);
    }

    @Test
    @DisplayName("상품 브랜드가 변경되고 가장 싼 브랜드가 캐시에 반영된다")
    void updateProduct_minBrandPrice() {
        // given
        var beforeBrandId = initBrandData("A", 10000);
        var afterBrandId = initBrandData("B", 10000);
        var productId = productService.addProduct(
            new AddProductDto(
                beforeBrandId,
                Category.SHOES,
                5000
            )
        );

        // when
        productService.updateProduct(
            productId,
            new UpdateProductDto(
                afterBrandId,
                Category.SHOES,
                5000
            )
        );

        // then
        assertThat(productRepository.findById(productId)).isPresent();
        assertThat(productRepository.findById(productId).get().getBrandId()).isEqualTo(afterBrandId);
        assertThat(cacheManager.get("BRAND:MIN", BrandMinCacheData.class)).isPresent();
        assertThat(cacheManager.get("BRAND:MIN", BrandMinCacheData.class).get().products().stream()
            .map(Product::getId)
            .collect(Collectors.toList()))
            .contains(productId);
    }

    @Test
    @DisplayName("상품이 삭제되고 카테고리별 가장 비싼 가격이 캐시에 반영된다")
    void removeProduct_maxPrice() {
        // given
        var brandId = initBrandData("A", 10000);
        var productId = productService.addProduct(
            new AddProductDto(
                brandId,
                Category.SHOES,
                20000
            )
        );

        // when
        productService.removeProduct(productId);

        // then
        assertThat(productRepository.findById(productId)).isEmpty();
        assertThat(cacheManager.get("CATEGORY:MAX:SHOES", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MAX:SHOES", Product.class).get().getId()).isNotEqualTo(productId);
    }

    @Test
    @DisplayName("상품이 삭제되고 카테고리별 가장 싼 가격이 캐시에 반영된다")
    void removeProduct_minPrice() {
        // given
        var brandId = initBrandData("A", 10000);
        var productId = productService.addProduct(
            new AddProductDto(
                brandId,
                Category.SHOES,
                5000
            )
        );

        // when
        productService.removeProduct(productId);

        // then
        assertThat(productRepository.findById(productId)).isEmpty();
        assertThat(cacheManager.get("CATEGORY:MIN:SHOES", Product.class)).isPresent();
        assertThat(cacheManager.get("CATEGORY:MIN:SHOES", Product.class).get().getId()).isNotEqualTo(productId);
    }

    @Test
    @DisplayName("브랜드와 상품이 추가되고 가장 싼 브랜드가 캐시에 반영된다")
    void addBrand_minBrandPrice() {
        // given
        var brandId = initBrandData("A", 10000);

        // when
        var dto = new AddBrandDto(
            "B",
            Arrays.stream(Category.values())
                .map(category ->
                    new AddBrandDto.Product(
                        category,
                        5000
                    )
                )
                .collect(Collectors.toList())
        );

        var newBrandId = productService.addBrand(dto);

        // then
        assertThat(brandRepository.findById(newBrandId)).isPresent();
        assertThat(cacheManager.get("BRAND:MIN", BrandMinCacheData.class)).isPresent();
        assertThat(cacheManager.get("BRAND:MIN", BrandMinCacheData.class).get().products().stream()
            .map(Product::getId)
            .collect(Collectors.toList()))
            .containsAll(productRepository.findAllByBrandId(newBrandId).stream()
                .map(Product::getId)
                .collect(Collectors.toList()));
    }

    private Long initBrandData(String name, Integer price) {
        Long brandId = brandRepository.save(
            new Brand(name)
        );

        Arrays.stream(Category.values())
            .forEach(category -> productRepository.save(
                new Product(
                    brandId,
                    category,
                    price
                )
            ));

        return brandId;
    }
}