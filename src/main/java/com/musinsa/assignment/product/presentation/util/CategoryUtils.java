package com.musinsa.assignment.product.presentation.util;

import com.musinsa.assignment.product.domain.Product.Category;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

public class CategoryUtils {

    public static Category convertFrom(String categoryString) {
        return Category.valueOf(PresentationCategory.from(categoryString).name());
    }

    public static String convertFrom(Category category) {
        return PresentationCategory.valueOf(category.name()).getDescription();
    }

    public static boolean hasAllCategories(List<String> categoryStrings) {
        return categoryStrings.stream()
            .map(PresentationCategory::from)
            .collect(Collectors.toList())
            .containsAll(List.of(PresentationCategory.values()));
    }

    @Getter
    private enum PresentationCategory {
        TOP("상의"),
        OUTER("아우터"),
        PANTS("바지"),
        SHOES("스니커즈"),
        BAG("가방"),
        HAT("모자"),
        SOCKS("양말"),
        ACCESSORY("액세서리"),
        ;

        private final String description;

        PresentationCategory(String description) {
            this.description = description;
        }

        public static PresentationCategory from(String description) {
            return Arrays.stream(values())
                .filter(presentationCategory -> presentationCategory.getDescription().equals(description))
                .findFirst()
                .orElseThrow();
        }
    }
}
