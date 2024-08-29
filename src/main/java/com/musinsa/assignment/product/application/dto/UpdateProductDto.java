package com.musinsa.assignment.product.application.dto;

import com.musinsa.assignment.product.domain.Product.Category;

public record UpdateProductDto(
    Long brandId,
    Category category,
    Integer price
) {
}
