package com.musinsa.assignment.product.domain;

import lombok.Getter;

@Getter
public class Product {
    private Long id;
    private Long brandId;
    private Category category;
    private Integer price;

    public Product(Long brandId,
                   Category category,
                   Integer price) {
        this.brandId = brandId;
        this.category = category;
        this.price = price;
    }

    public Product(Long id,
                   Long brandId,
                   Category category,
                   Integer price) {
        this.id = id;
        this.brandId = brandId;
        this.category = category;
        this.price = price;
    }

    public void update(Long brandId,
                       Category category,
                       Integer price) {
        this.brandId = brandId;
        this.category = category;
        this.price = price;
    }

    public enum Category {
        TOP,
        OUTER,
        PANTS,
        SHOES,
        BAG,
        HAT,
        SOCKS,
        ACCESSORY
    }
}
