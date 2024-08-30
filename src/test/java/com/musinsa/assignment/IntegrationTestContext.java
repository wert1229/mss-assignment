package com.musinsa.assignment;

import com.musinsa.assignment.product.application.ProductQueryService;
import com.musinsa.assignment.product.application.ProductService;
import com.musinsa.assignment.product.application.contract.BrandRepository;
import com.musinsa.assignment.product.application.contract.CacheManager;
import com.musinsa.assignment.product.application.contract.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public abstract class IntegrationTestContext {

    @Autowired
    protected ProductService productService;

    @Autowired
    protected ProductQueryService productQueryService;

    @Autowired
    protected BrandRepository brandRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected CacheManager cacheManager;

}
