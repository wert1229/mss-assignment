package com.musinsa.assignment.product.presentation;

import com.musinsa.assignment.common.web.ApiResponse;
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

    @PostMapping("/v1/brand")
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
}
