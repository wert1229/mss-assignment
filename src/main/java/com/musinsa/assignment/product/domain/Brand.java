package com.musinsa.assignment.product.domain;

import lombok.Getter;

@Getter
public class Brand {
    private Long id;
    private String name;

    public Brand(String name) {
        this.name = name;
    }

    public Brand(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
