package com.zwind.identityservice.interceptors;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.zwind.common_lib.exception.HttpException;

@GrpcGlobalServerInterceptor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GrpcGlobalExceptionInterceptor implements ServerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(GrpcGlobalExceptionInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> next) {
        ServerCall.Listener<ReqT> delegate;
        try {
            delegate = next.startCall(call, metadata);
        }catch (Throwable t) {
            close(call, t);
            return new ServerCall.Listener<ReqT>() {};
        }

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
            @Override
            public void onHalfClose() {
                try{
                    super.onHalfClose();
                } catch (Throwable t) {
                    close(call, t);
                }
            }
        };
    }

    private void close(ServerCall<?, ?> call, Throwable t) {
        log.error(t.getMessage());
        Status status = mapToStatus(t);
        call.close(status, new Metadata());
    }

    private Status mapToStatus(Throwable t) {
        if (t instanceof StatusException se) {
            return se.getStatus();
        }

        if (t instanceof StatusRuntimeException sre) {
            return sre.getStatus();
        }

        if (t instanceof HttpException e) {
            return switch (e.getHttpError().getCode()) {
                case "UNAUTHENTICATED" ->
                        Status.UNAUTHENTICATED.withDescription(e.getMessage());
                case "PERMISSION_DENIED" ->
                        Status.PERMISSION_DENIED.withDescription(e.getMessage());
                case "NOT_FOUND" ->
                        Status.NOT_FOUND.withDescription(e.getMessage());
                case "INVALID_ARGUMENT" ->
                        Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                default ->
                        Status.INTERNAL.withDescription(e.getMessage());
            };
        }

        return Status.INTERNAL
                .withDescription("Internal server error")
                .withCause(t);
    }
}
