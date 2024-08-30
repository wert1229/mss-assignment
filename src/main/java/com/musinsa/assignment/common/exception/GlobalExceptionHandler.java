package com.musinsa.assignment.common.exception;

import static com.musinsa.assignment.common.support.Status.INVALID_PARAMETER;

import com.musinsa.assignment.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String SERVER_ERROR_MESSAGE = "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApplicationException.class)
    public ApiResponse<Void> handleApplicationException(ApplicationException e) {
        log.error(
            "{}. status = {}, message = {}",
            e.getClass().getSimpleName(),
            e.status(),
            e.message(),
            e
        );

        return ApiResponse.custom(
            e.status(),
            e.message()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(
            "{}. message = {}",
            e.getClass().getSimpleName(),
            e.getMessage(),
            e
        );

        return ApiResponse.custom(
            INVALID_PARAMETER,
            e.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleServerError(Exception e) {
        log.error(
            "{}. message = {}",
            e.getClass().getSimpleName(),
            e.getMessage(),
            e
        );

        return ApiResponse.error(SERVER_ERROR_MESSAGE);
    }
}
