package com.musinsa.assignment.common.support;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        setVisibility(
            PropertyAccessor.FIELD,
            JsonAutoDetect.Visibility.ANY
        );
        registerModules(
            new JavaTimeModule(),
            new Jdk8Module(),
            new CustomModule()
        );
        disable(
            FAIL_ON_UNKNOWN_PROPERTIES
        );
        disable(
            WRITE_DATES_AS_TIMESTAMPS,
            WRITE_DURATIONS_AS_TIMESTAMPS
        );
    }

    private static class CustomModule extends SimpleModule {
        public CustomModule() {
            addSerializer(
                LocalTime.class,
                new LocalTimeSerializer(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            );
            addSerializer(
                ZonedDateTime.class,
                new ZonedDateTimeSerializer(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                )
            );
        }
    }
}
