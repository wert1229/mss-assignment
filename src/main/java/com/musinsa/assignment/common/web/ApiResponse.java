package com.musinsa.assignment.common.web;

import com.musinsa.assignment.common.support.Status;
import java.time.ZonedDateTime;

public class ApiResponse<T> {
    private final Status status;
    private final String message;
    private final ZonedDateTime serverDatetime;
    private final T data;

    private ApiResponse(Status status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.serverDatetime = ZonedDateTime.now();
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(
            Status.SUCCESS,
            Status.SUCCESS.message(),
            data
        );
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(
            Status.ERROR,
            message,
            null
        );
    }

    public static <T> ApiResponse<T> custom(Status status,
                                            String message) {
        return custom(
            status,
            message,
            null
        );
    }

    public static <T> ApiResponse<T> custom(Status status,
                                            String message,
                                            T data) {
        return new ApiResponse<>(
            status,
            message,
            data
        );
    }
}
