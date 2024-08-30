package com.musinsa.assignment.common.support;

import com.musinsa.assignment.product.application.ProductService;
import com.musinsa.assignment.product.application.dto.AddBrandDto;
import com.musinsa.assignment.product.application.dto.AddBrandDto.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
    prefix = "application.runner",
    value = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private static final String FILE_PATH = "csv/brand.csv";

    private final ProductService productService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var csv = new ClassPathResource(FILE_PATH);
        var br = new BufferedReader(new InputStreamReader(csv.getInputStream()));

        String line;
        while ((line = br.readLine()) != null) {
            var col = line.split(",");
            productService.addBrand(
                new AddBrandDto(
                    col[0],
                    List.of(
                        new Product(Category.TOP, Integer.valueOf(col[1])),
                        new Product(Category.OUTER, Integer.valueOf(col[2])),
                        new Product(Category.PANTS, Integer.valueOf(col[3])),
                        new Product(Category.SHOES, Integer.valueOf(col[4])),
                        new Product(Category.BAG, Integer.valueOf(col[5])),
                        new Product(Category.HAT, Integer.valueOf(col[6])),
                        new Product(Category.SOCKS, Integer.valueOf(col[7])),
                        new Product(Category.ACCESSORY, Integer.valueOf(col[8]))
                    )
                )
            );
        }
    }
}
