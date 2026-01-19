package com.zwind.grpc_security_starter.interceptors;

import io.grpc.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@GrpcGlobalServerInterceptor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrpcSecurityInterceptor implements ServerInterceptor {
    GrpcAuthenticationProvider authenticationProvider;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        GrpcAuthenticationToken authenticated = authenticationProvider.authenticate(metadata);

        Context context = Context.current().withValue(GrpcSecurityContext.AUTH_KEY, authenticated);

        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }
}
