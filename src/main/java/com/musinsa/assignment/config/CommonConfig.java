package com.musinsa.assignment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.assignment.common.support.CustomObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new CustomObjectMapper();
    }
}
