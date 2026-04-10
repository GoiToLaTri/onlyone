package com.zwind.userservice.provider;

import com.zwind.grpc_security_starter.interceptors.GrpcAuthenticationProvider;
import com.zwind.grpc_security_starter.interceptors.GrpcAuthenticationToken;
import io.grpc.Metadata;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationProvider implements GrpcAuthenticationProvider {
    @Value("${jwt.signerKey}")
    private String JWT_SECRET_KEY;

    @Override
    public GrpcAuthenticationToken authenticate(Metadata metadata) {
        log.info(":::: metadata {}", metadata);

        if(metadata == null) return GrpcAuthenticationToken.unauthenticated();

        String rawToken = metadata.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));
        if(rawToken == null || !rawToken.startsWith("Bearer "))
            return GrpcAuthenticationToken.unauthenticated();
        String token =rawToken.replaceFirst("^Bearer\\s+", "");

        Claims claims = decodeToken(token);
        if(claims == null) return GrpcAuthenticationToken.unauthenticated();
        log.info(":::: claims {}", claims);
        String scope = claims.get("scope", String.class);
        List<String> scopes = scope == null
                ? List.of()
                : Arrays.asList(scope.split("\\s+"));

        return GrpcAuthenticationToken.authenticated(claims.getSubject(), null, scopes);
    }

    private Claims decodeToken(String token){
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
