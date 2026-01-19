package com.zwind.grpc_security_starter.interceptors;

import io.grpc.Context;
import io.grpc.Status;

public class GrpcSecurityContext {
    public static final Context.Key<GrpcAuthenticationToken> AUTH_KEY = Context.key("authentication");

    private GrpcSecurityContext(){}

    /**
     * Lấy thông tin xác thực của user hiện tại.
     * @return GrpcAuthenticationToken (luôn khác null nhờ Interceptor xử lý)
     * @throws RuntimeException nếu chưa xác thực (để bảo vệ các hàm private)
     */
    public static GrpcAuthenticationToken getCurrentAuth() {
        GrpcAuthenticationToken authentication = AUTH_KEY.get();
        if (authentication == null || !authentication.isAuthenticated())
            throw Status.UNAUTHENTICATED
                    .withDescription("Unauthenticated")
                    .asRuntimeException();

        return authentication;
    }

    /**
     * Lấy thông tin xác thực mà không ném lỗi.
     * Dùng cho các hàm Public nhưng vẫn muốn biết user là ai (nếu có).
     */
    public static GrpcAuthenticationToken getOptionalAuth() {
        return AUTH_KEY.get();
    }

}
