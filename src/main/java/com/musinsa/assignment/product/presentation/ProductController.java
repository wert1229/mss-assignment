package com.musinsa.assignment.product.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.assignment.common.web.ApiResponse;
import com.musinsa.assignment.product.application.ProductQueryService;
import com.musinsa.assignment.product.application.ProductService;
import com.musinsa.assignment.product.application.dto.AddBrandDto;
import com.musinsa.assignment.product.application.dto.AddProductDto;
import com.musinsa.assignment.product.application.dto.UpdateProductDto;
import com.musinsa.assignment.product.application.exception.CategoryEmptyException;
import com.musinsa.assignment.product.presentation.ProductController.AddBrandRequest.Product;
import com.musinsa.assignment.product.presentation.util.CategoryUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductQueryService productQueryService;

    @PostMapping("/v1/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, Long>> addProduct(@Valid @RequestBody AddProductRequest request) {
        var id = productService.addProduct(
            new AddProductDto(
                request.brandId(),
                CategoryUtils.convertFrom(request.category()),
                request.price()
            )
        );

        return ApiResponse.success(
            Map.of(
                "id", id
            )
        );
    }

    public record AddProductRequest(
        @NotNull
        Long brandId,
        @NotNull
        String category,
        @Positive
        Integer price
    ) {
    }

    @PutMapping("/v1/products/{productId}")
    public ApiResponse<Void> updateProduct(@PathVariable Long productId, @Valid @RequestBody UpdateProductRequest request) {
        productService.updateProduct(
            productId,
            new UpdateProductDto(
                request.brandId(),
                CategoryUtils.convertFrom(request.category()),
                request.price()
            )
        );
        return ApiResponse.success();
    }

    public record UpdateProductRequest(
        @NotNull
        Long brandId,
        @NotNull
        String category,
        @Positive
        Integer price
    ) {
    }

    @DeleteMapping("/v1/products/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        productService.removeProduct(productId);
        return ApiResponse.success();
    }

    @PostMapping("/v1/brands")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, Long>> addBrand(@Valid @RequestBody AddBrandRequest request) {
        var hasAllCategories = CategoryUtils.hasAllCategories(
            request.products().stream()
                .map(Product::category)
                .collect(Collectors.toList())
        );

        if (!hasAllCategories) {
            throw new CategoryEmptyException();
        }

        var id = productService.addBrand(
            new AddBrandDto(
                request.brandName(),
                request.products().stream()
                    .map(product -> new AddBrandDto.Product(
                        CategoryUtils.convertFrom(product.category()),
                        product.price()
                    ))
                    .collect(Collectors.toList())
            )
        );

        return ApiResponse.success(
            Map.of(
                "id", id
            )
        );
    }

    public record AddBrandRequest(
        @NotEmpty
        String brandName,
        List<Product> products
    ) {
        public record Product(
            String category,
            Integer price
        ) {
        }
    }

    @GetMapping("/v1/products/categories-min-prices")
    public ApiResponse<CategoriesMinPricesResponse> getCategoriesMinPrices() {
        return ApiResponse.success(
            productQueryService.getCategoriesMinPrices()
        );
    }

    public record CategoriesMinPricesResponse(
        @JsonProperty("총액")
        Integer totalPrice,
        @JsonProperty("카테고리")
        List<PresentationProduct> products
    ) {
    }

    @GetMapping("/v1/products/brand-min-prices")
    public ApiResponse<Map<String, BrandMinPricesResponse>> getBrandMinPrices() {
        return ApiResponse.success(
            Map.of(
                "최저가", productQueryService.getBrandMinPrices()
            )
        );
    }

    public record BrandMinPricesResponse(
        @JsonProperty("총액")
        Integer totalPrice,
        @JsonProperty("카테고리")
        List<PresentationProduct> products,
        @JsonProperty("브랜드")
        String brandName
    ) {
    }

    @GetMapping("/v1/products/category-min-max-prices")
    public ApiResponse<CategoryMinMaxPricesResponse> getCategoryMinMaxPrices(String category) {
        return ApiResponse.success(
            productQueryService.getCategoryMinMaxPrices(CategoryUtils.convertFrom(category))
        );
    }

    public record CategoryMinMaxPricesResponse(
        @JsonProperty("카테고리")
        String category,
        @JsonProperty("최저가")
        List<PresentationProduct> minPrices,
        @JsonProperty("최고가")
        List<PresentationProduct> maxPrices
    ) {
    }

    public record PresentationProduct(
        @JsonProperty("카테고리")
        String category,
        @JsonProperty("브랜드")
        String brand,
        @JsonProperty("가격")
        Integer price
    ) {
    }
}
