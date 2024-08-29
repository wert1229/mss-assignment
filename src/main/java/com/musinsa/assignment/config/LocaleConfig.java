package com.musinsa.assignment.config;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocaleConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
