package com.zwind.identityservice.exception;

import com.zwind.common_lib.dto.response.ApiResponse;
import com.zwind.common_lib.exception.HttpError;
import com.zwind.common_lib.exception.HttpException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> runtimeExceptionHandling(RuntimeException exception) {
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(HttpError.SERVER_ERROR.getCode());
        apiResponse.setMessage(HttpError.SERVER_ERROR.getMessage());

        log.error(exception.getMessage());
        return ResponseEntity.status(HttpError.SERVER_ERROR.getHttpStatus().value()).body(apiResponse);
    }

    @ExceptionHandler(value = HttpException.class)
    ResponseEntity<ApiResponse<?>> appExceptionHandling(HttpException exception) {
        HttpError httpError = exception.getHttpError();
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(httpError.getCode());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(httpError.getHttpStatus().value()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> accessDeniedExceptionHandling(AccessDeniedException exception) {
        HttpError httpError = HttpError.FORBIDDEN;
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(httpError.getCode());
        apiResponse.setMessage(httpError.getMessage());

        return ResponseEntity.status(httpError.getHttpStatus().value()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> validationErrorHandling (MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        HttpError httpError = HttpError.INVALID_KEY;
        try {
            httpError = HttpError.valueOf(enumKey);
        } catch (IllegalArgumentException e) {}

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(httpError.getCode());
        apiResponse.setMessage(httpError.getMessage());

        return ResponseEntity.status(httpError.getHttpStatus().value()).body(apiResponse);
    }
}
