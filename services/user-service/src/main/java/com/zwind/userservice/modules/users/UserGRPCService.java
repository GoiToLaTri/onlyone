package com.zwind.userservice.modules.users;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.zwind.grpc.user.UserRequest;
import com.zwind.grpc.user.UserResponse;
import com.zwind.grpc.user.UserServiceGrpc;
import com.zwind.grpc_security_starter.interceptors.GrpcAuthenticationToken;
import com.zwind.grpc_security_starter.interceptors.GrpcSecure;
import com.zwind.grpc_security_starter.interceptors.GrpcSecurityContext;
import com.zwind.userservice.modules.users.dto.UserResponseDto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.ZoneId;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserGRPCService extends UserServiceGrpc.UserServiceImplBase {
    UserService userService;

    @Override
    public void findByPublicId(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        String publicId = request.getPublicId();
        UserResponseDto user = userService.findByPublicId(publicId);

        UserResponse response = UserResponse.newBuilder()
                .setPublicId(user.getPublicId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @GrpcSecure
    public void profile(Empty request, StreamObserver<UserResponse> responseObserver) {
        GrpcAuthenticationToken context = GrpcSecurityContext.getCurrentAuth();
        UserResponseDto user = userService.findByAccountId(context.getUserId());
        log.info(":::: user {}", user);
        if (user == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Profile not found for this account")
                            .asRuntimeException()
            );
            return;
        }

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(user.getCreatedAt().atZone(ZoneId.systemDefault()).toEpochSecond())
                .setNanos(user.getCreatedAt().getNano())
                .build();

        UserResponse response = UserResponse.newBuilder()
                .setPublicId(user.getPublicId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setCreatedAt(timestamp)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
