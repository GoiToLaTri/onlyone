package com.zwind.identityservice.interceptors;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@GrpcGlobalServerInterceptor
@Slf4j
public class MetadataInterceptor implements ServerInterceptor {
    public static final Context.Key<Metadata> METADATA_CONTEXT_KEY =
            Context.key("currentMetadata");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {
        // 1. Debug để chắc chắn Header có tới
        log.info("Interceptor received headers: {}", metadata.toString());

        // 2. Tạo một Context mới dựa trên Context hiện tại và đính kèm Metadata vào
        Context context = Context.current().withValue(METADATA_CONTEXT_KEY, metadata);

        // 3. Quan trọng: Sử dụng Contexts.interceptCall để gRPC tự động gắn Context này
        // vào luồng xử lý Request của các hàm Service phía sau.
        // Điều này giúp hàm introspect() có thể đọc được Context trên đúng Thread đó.
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }
}
