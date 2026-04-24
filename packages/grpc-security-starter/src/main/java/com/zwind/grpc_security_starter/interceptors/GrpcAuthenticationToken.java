package com.zwind.grpc_security_starter.interceptors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrpcAuthenticationToken {
    String accountId;
    String deviceId;
    List<String> scopes;
    boolean authenticated;

    private GrpcAuthenticationToken(String accountId, String deviceId, List<String> scopes, boolean authenticated) {
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.scopes = scopes;
        this.authenticated = authenticated;
    }


    /**
     * Tạo đối tượng cho trường hợp CHƯA xác thực (Token trống hoặc lỗi)
     */
    public static GrpcAuthenticationToken unauthenticated() {
        return new GrpcAuthenticationToken(null, null, null, false);
    }

    /**
     * Tạo đối tượng sau khi đã xác thực THÀNH CÔNG từ Redis/DB
     */
    public static GrpcAuthenticationToken authenticated(String accountId, String deviceId, List<String> scopes) {
        return new GrpcAuthenticationToken(accountId, deviceId, scopes, true);
    }
}
