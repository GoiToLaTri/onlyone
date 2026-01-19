package com.zwind.userservice.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum AppError {
    FORBIDDEN("FORBIDDEN", "Access denied", HttpStatus.FORBIDDEN),
    SERVER_ERROR("INTERNAL_SERVER_ERROR","Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED("UNAUTHENTICATED", "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_KEY("BAD_REQUEST", "Uncategorized error", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED("BAD_REQUEST", "Name required", HttpStatus.BAD_REQUEST),
    NAME_INVALID("BAD_REQUEST", "Name invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("BAD_REQUEST", "Password required", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("BAD_REQUEST", "Password invalid", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED("BAD_REQUEST", "Email required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("BAD_REQUEST", "Email invalid", HttpStatus.BAD_REQUEST),
    USER_EXISTS("BAD_REQUEST", "User existed", HttpStatus.BAD_REQUEST),
    USER_PENDING("ACCEPTED", "User is pending", HttpStatus.ACCEPTED),
    USER_NOT_EXISTS("NOT_FOUND", "User not exists", HttpStatus.NOT_FOUND),
    USER_NOT_EXISTS_ROLES("NOT_FOUND", "User %s not exists roles %s", HttpStatus.NOT_FOUND),
    UID_REQUIRED("BAD_REQUEST", "User id required", HttpStatus.BAD_REQUEST),
    ROLE_NAME_REQUIRED("BAD_REQUEST", "Role name required", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTS("NOT_FOUND", "Role %s not exists", HttpStatus.NOT_FOUND),
    PERMISSION_NAME_REQUIRED("BAD_REQUEST", "Permission name required", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTS("NOT_FOUND", "Permission %s not exists", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_EXISTS_IN_ROLE("BAD_REQUEST", "Permissions %s not exists in %s role", HttpStatus.BAD_REQUEST),
    CANNOT_CHANGE_SELF_ROLE("BAD_REQUEST", "Cannot change self role", HttpStatus.BAD_REQUEST),
    REQUEST_TOKEN_REQUIRED("BAD_REQUEST", "Request token required", HttpStatus.BAD_REQUEST),
    SESSION_NOT_EXISTS("NOT_FOUND", "Session not exists", HttpStatus.BAD_REQUEST),
    TOKEN_ALREADY_USED("BAD_REQUEST", "Token already used", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_REQUIRED("BAD_REQUEST", "Access token required", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_REQUIRED("BAD_REQUEST", "Refresh token required", HttpStatus.BAD_REQUEST),
    ;

    String code;
    String message;
    HttpStatusCode httpStatusCode;
}
