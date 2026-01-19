package com.zwind.identityservice.interceptors;

import com.zwind.identityservice.exception.AppException;
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@GrpcGlobalServerInterceptor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GrpcGlobalExceptionInterceptor implements ServerInterceptor {
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

        if (t instanceof AppException ae) {
            return switch (ae.getAppError().getCode()) {
                case "UNAUTHENTICATED" ->
                        Status.UNAUTHENTICATED.withDescription(ae.getMessage());
                case "PERMISSION_DENIED" ->
                        Status.PERMISSION_DENIED.withDescription(ae.getMessage());
                case "NOT_FOUND" ->
                        Status.NOT_FOUND.withDescription(ae.getMessage());
                case "INVALID_ARGUMENT" ->
                        Status.INVALID_ARGUMENT.withDescription(ae.getMessage());
                default ->
                        Status.INTERNAL.withDescription(ae.getMessage());
            };
        }

        return Status.INTERNAL
                .withDescription("Internal server error")
                .withCause(t);
    }
}
