package com.musinsa.assignment.common.support;

public enum Status {
    SUCCESS("성공"),
    ERROR("에러"),
    INVALID_PARAMETER("파라미터가 올바르지 않습니다."),

    BRAND_NOT_FOUND("해당 브랜드를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND("해당 상품를 찾을 수 없습니다."),
    CATEGORY_EMPTY("카테고리에 최소한 하나의 상품이 존재해야 합니다."),
    ;

    private final String message;

    Status(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
