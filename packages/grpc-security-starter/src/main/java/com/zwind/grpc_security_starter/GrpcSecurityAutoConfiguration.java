package com.zwind.grpc_security_starter;

import com.zwind.grpc_security_starter.interceptors.GrpcAuthenticationProvider;
import com.zwind.grpc_security_starter.interceptors.GrpcSecurityAspect;
import com.zwind.grpc_security_starter.interceptors.GrpcSecurityInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = "com.zwind.grpc_security_starter")
public class GrpcSecurityAutoConfiguration {
    @Bean
    @GrpcGlobalServerInterceptor
    public GrpcSecurityInterceptor grpcSecurityInterceptor(GrpcAuthenticationProvider provider){
        return new GrpcSecurityInterceptor(provider);
    }

    @Bean
    public GrpcSecurityAspect grpcSecurityAspect() {
        return new GrpcSecurityAspect();
    }
}
