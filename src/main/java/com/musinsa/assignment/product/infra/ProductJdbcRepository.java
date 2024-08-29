package com.musinsa.assignment.product.infra;

import com.musinsa.assignment.product.application.contract.ProductRepository;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductJdbcRepository implements ProductRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long save(Product product) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        """
            INSERT INTO product (brand_id, category, price)
            VALUES (:brandId, :category, :price)
            """,
            new MapSqlParameterSource(Map.of(
                "brandId", product.getBrandId(),
                "category", product.getCategory(),
                "price", product.getPrice()
            )),
            keyHolder
        );
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void delete(Product product) {
        jdbcTemplate.update(
            """
            DELETE FROM product
            WHERE id = :id
            """,
            Map.of(
                "id", product.getId()
            )
        );
    }

    @Override
    public Optional<Product> findById(Long id) {
        var brand = jdbcTemplate.queryForObject(
        """
            SELECT id, brand_id, category, price
            FROM product
            WHERE id = :id
            """,
            Map.of(
                "id", id
            ),
            new BeanPropertyRowMapper<Product>()
        );

        return Optional.ofNullable(brand);
    }

    @Override
    public Integer countByBrandAndCategory(Long brandId, Category category) {
        return jdbcTemplate.queryForObject(
        """
            SELECT COUNT(*)
            FROM product
            WHERE brand_id = :brandId
            AND category = :category
            """,
            Map.of(
                "brandId", brandId,
                "category", category
            ),
            Integer.class
        );
    }

    @Override
    public List<Product> findMinPriceProductsByCategory() {
        return jdbcTemplate.query(
        """
            SELECT
                p.id,
                p.brand_id,
                p.category,
                p.price
            FROM product p
            WHERE p.price = (
                SELECT MIN(p2.price)
                FROM product p2
                WHERE p2.category = p1.category
            )
            """,
            new BeanPropertyRowMapper<>()
        );
    }

    @Override
    public List<Product> findMaxPriceProductsByCategory() {
        return jdbcTemplate.query(
        """
            SELECT
                p.id,
                p.brand_id,
                p.category,
                p.price
            FROM product p
            WHERE p.price = (
                SELECT MAX(p2.price)
                FROM product p2
                WHERE p2.category = p1.category
            )
            """,
            new BeanPropertyRowMapper<>()
        );
    }

    @Override
    public List<Product> findAllByBrandMinPrice() {
        return jdbcTemplate.query(
        """
            SELECT
                 p.id,
                 p.brand_id,
                 p.category,
                 p.price
            FROM
                 product p
            JOIN (
                 SELECT
                     brand_id,
                     category,
                     MIN(price) AS min_price
                 FROM
                     product
                 GROUP BY
                     brand_id,
                     category
            ) AS bmp ON p.brand_id = bmp.brand_id
                    AND p.category = bmp.category
                    AND p.price = bmp.min_price
            JOIN (
                SELECT
                    brand_id,
                    SUM(min_price) AS total_min_price
                FROM (
                    SELECT
                        brand_id,
                        category,
                        MIN(price) AS min_price
                    FROM
                        product
                    GROUP BY
                        brand_id,
                        category
                ) AS brand_min_prices
                GROUP BY
                    brand_id
                ORDER BY
                    total_min_price
                LIMIT 1
            ) AS cb ON p.brand_id = cb.brand_id
            """,
            new BeanPropertyRowMapper<>()
        );
    }
}
