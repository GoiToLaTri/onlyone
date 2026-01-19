package com.zwind.grpc_security_starter.interceptors;

import io.grpc.Status;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Arrays;

@Aspect
public class GrpcSecurityAspect {
    // Chặn trước khi bất kỳ hàm nào có gắn @GrpcSecure được thực thi
    @Before("@annotation(grpcSecure)")
    public void checkSecurity(GrpcSecure grpcSecure){
        GrpcAuthenticationToken authentication = GrpcSecurityContext.getCurrentAuth();

        String[] requiredRoles = grpcSecure.value();
        if(requiredRoles.length > 0) {
            boolean hasRole = Arrays.stream(requiredRoles)
                    .anyMatch(role -> authentication.getScopes().contains(role));

            if(!hasRole)
                throw Status.PERMISSION_DENIED
                        .withDescription("Access denied")
                        .asRuntimeException();
        }
    }
}
