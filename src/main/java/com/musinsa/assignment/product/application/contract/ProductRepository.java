package com.musinsa.assignment.product.application.contract;

import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Long save(Product product);

    void delete(Long id);

    Optional<Product> findById(Long id);

    Integer countByBrandAndCategory(Long brandId, Category category);

    List<Product> findAllByBrandId(Long brandId);

}
