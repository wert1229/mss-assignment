package com.musinsa.assignment.product.application.dto;

import com.musinsa.assignment.product.domain.Product.Category;
import java.util.List;

public record AddBrandDto(
    String brandName,
    List<Product> products
) {
    public record Product(
        Category category,
        Integer price
    ) {
    }
}
