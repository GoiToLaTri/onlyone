package com.zwind.grpc_security_starter.interceptors;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcSecure {
    /**
     * Danh sách các Role được phép truy cập.
     * Nếu để trống, chỉ cần người dùng đã đăng nhập (authenticated = true) là được.
     */
    String[] value() default {};
}
