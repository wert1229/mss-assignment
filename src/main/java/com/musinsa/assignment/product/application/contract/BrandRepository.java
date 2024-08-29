package com.musinsa.assignment.product.application.contract;

import com.musinsa.assignment.product.domain.Brand;
import java.util.Optional;

public interface BrandRepository {

    Long save(Brand brand);

    Optional<Brand> findById(Long id);

}
