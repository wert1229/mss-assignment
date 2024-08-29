package com.musinsa.assignment.product.infra;

import com.musinsa.assignment.product.application.contract.BrandRepository;
import com.musinsa.assignment.product.domain.Brand;
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
public class BrandJdbcRepository implements BrandRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long save(Brand brand) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        """
            INSERT INTO brand (name)
            VALUES (:name)
            """,
            new MapSqlParameterSource("name", brand.getName()),
            keyHolder
        );
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Optional<Brand> findById(Long id) {
        var brand = jdbcTemplate.queryForObject(
        """
            SELECT id, name
            FROM brand
            WHERE id = :id
            """,
            Map.of(
                "id", id
            ),
            new BeanPropertyRowMapper<Brand>()
        );

        return Optional.ofNullable(brand);
    }
}
