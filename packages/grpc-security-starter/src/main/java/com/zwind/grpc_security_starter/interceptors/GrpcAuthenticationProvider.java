package com.zwind.grpc_security_starter.interceptors;

import io.grpc.Metadata;

public interface GrpcAuthenticationProvider {
    /**
     * Hàm thực hiện xác thực người dùng.
     * @param metadata Chứa toàn bộ headers (để lấy Token, DeviceId, v.v.)
     * @return GrpcAuthenticationToken (có thể là authenticated hoặc unauthenticated)
     */
    GrpcAuthenticationToken authenticate(Metadata metadata);
}
