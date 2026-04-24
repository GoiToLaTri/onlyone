package com.zwind.identityservice.exception;

import com.zwind.common_lib.dto.response.ApiResponse;
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

        apiResponse.setCode(AppError.SERVER_ERROR.getCode());
        apiResponse.setMessage(AppError.SERVER_ERROR.getMessage());

        log.error(exception.getMessage());
        return ResponseEntity.status(AppError.SERVER_ERROR.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> appExceptionHandling(AppException exception) {
        AppError appError = exception.getAppError();
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(appError.getCode());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(appError.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> accessDeniedExceptionHandling(AccessDeniedException exception) {
        AppError appError = AppError.FORBIDDEN;
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(appError.getCode());
        apiResponse.setMessage(appError.getMessage());

        return ResponseEntity.status(appError.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> validationErrorHandling (MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        AppError appError = AppError.INVALID_KEY;
        try {
            appError = AppError.valueOf(enumKey);
        } catch (IllegalArgumentException e) {}

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(appError.getCode());
        apiResponse.setMessage(appError.getMessage());

        return ResponseEntity.status(appError.getHttpStatusCode()).body(apiResponse);
    }
}
