package com.musinsa.assignment.product.infra;

import com.musinsa.assignment.product.application.contract.ProductRepository;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Product.Category;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
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
        if (product.getId() == null) {
            return insert(product);
        } else {
            return update(product);
        }
    }

    private Long insert(Product product) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        """
            INSERT INTO product (brand_id, category, price)
            VALUES (:brandId, :category, :price)
            """,
            new MapSqlParameterSource(Map.of(
                "brandId", product.getBrandId(),
                "category", product.getCategory().name(),
                "price", product.getPrice()
            )),
            keyHolder
        );
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long update(Product product) {
        jdbcTemplate.update(
            """
            UPDATE product
            SET brand_id = :brandId, category = :category, price = :price
            WHERE id = :id
            """,
            new MapSqlParameterSource(Map.of(
                "brandId", product.getBrandId(),
                "category", product.getCategory().name(),
                "price", product.getPrice(),
                "id", product.getId()
            ))
        );
        return product.getId();
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
        try {
            var brand = jdbcTemplate.queryForObject(
                """
                    SELECT id, brand_id, category, price
                    FROM product
                    WHERE id = :id
                    """,
                Map.of(
                    "id", id
                ),
                new ProductRowMapper()
            );
            return Optional.ofNullable(brand);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findAllByBrandId(Long brandId) {
        return jdbcTemplate.query(
            """
                SELECT id, brand_id, category, price
                FROM product
                WHERE brand_id = :brandId
                """,
            Map.of(
                "brandId", brandId
            ),
            new ProductRowMapper()
        );
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
                "category", category.name()
            ),
            Integer.class
        );
    }

    @Override
    public List<Product> findMinPriceProductsByCategory() {
        return jdbcTemplate.query(
            """
            SELECT
                p.id as id,
                p.brand_id as brand_id,
                p.category as category,
                p.price as price
            FROM product p
            JOIN
                (SELECT
                    category,
                    MAX(id) AS min_product_id
                FROM
                    product
                WHERE
                    (category, price) IN (
                        SELECT
                            category,
                            MIN(price)
                        FROM
                            product
                        GROUP BY
                            category
                    )
                GROUP BY
                    category
                ) min_products
            ON p.id = min_products.min_product_id
            """,
            new ProductRowMapper()
        );
    }

    @Override
    public List<Product> findMaxPriceProductsByCategory() {
        return jdbcTemplate.query(
        """
            SELECT
                p.id as id,
                p.brand_id as brand_id,
                p.category as category,
                p.price as price
            FROM product p
            JOIN
                (SELECT
                    category,
                    MAX(id) AS max_product_id
                FROM
                    product
                WHERE
                    (category, price) IN (
                        SELECT
                            category,
                            MAX(price)
                        FROM
                            product
                        GROUP BY
                            category
                    )
                GROUP BY
                    category
                ) max_products
            ON p.id = max_products.max_product_id
            """,
            new ProductRowMapper()
        );
    }

    @Override
    public List<Product> findAllByBrandMinPrice() {
        return jdbcTemplate.query(
        """
            SELECT
                p.id as id,
                p.brand_id as brand_id,
                p.category as category,
                p.price as price
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
            new ProductRowMapper()
        );
    }

    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Product(
                rs.getLong("id"),
                rs.getLong("brand_id"),
                Category.valueOf(rs.getString("category")),
                rs.getInt("price")
            );
        }
    }
}
