package com.zwind.identityservice.modules.authentication;


import com.zwind.grpc.authentication.AuthenticationServiceGrpc;
import com.zwind.grpc.authentication.IntrospectRequest;
import com.zwind.grpc.authentication.IntrospectResponse;
import com.zwind.grpc_security_starter.interceptors.GrpcSecure;
import com.zwind.grpc_security_starter.interceptors.GrpcSecurityContext;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationGRPCService extends
        AuthenticationServiceGrpc.AuthenticationServiceImplBase {
        AuthenticationService authenticationService;
    @Override
    @GrpcSecure
    public void introspect(IntrospectRequest request,
                           StreamObserver<IntrospectResponse> responseObserver) {
        String accountId = GrpcSecurityContext.getCurrentAuth().getUserId();
        String token = authenticationService.introspect(accountId);
        IntrospectResponse response = IntrospectResponse.newBuilder()
                .setToken(token)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
