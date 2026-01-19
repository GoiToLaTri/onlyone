package com.zwind.userservice.interceptors;

import io.grpc.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@GrpcGlobalServerInterceptor
@Slf4j
public class JwtTokenInterceptor implements ServerInterceptor {
    private static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata
            .Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Value("${jwt.signerKey}")
    private String JWT_SECRET_KEY;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String rawToken = metadata.get(AUTHORIZATION_METADATA_KEY);

        if(rawToken == null || !rawToken.startsWith("Bearer ")){
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Missing or invalid token"),
                    new Metadata());
            return new ServerCall.Listener<ReqT>(){};
        }

        String token = rawToken.substring(7);
        log.info(":::: token {}", token);
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            log.info(":::: claims {}",claims);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken("onlyone", null);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            log.error(e.getMessage());
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Token expired or invalid"),
                    new Metadata());

            return new ServerCall.Listener<ReqT>() {};
        }

        return serverCallHandler.startCall(serverCall, metadata);
    }
}
