package com.zwind.identityservice.modules.jwt;

import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zwind.identityservice.utils.JwtUtil;
import com.zwind.identityservice.utils.KeyUtil;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtService implements IJwtService{
    @NonFinal
    @Value("${jwt.public-key-path}")
    String publicKeyPath;

    @Override
    public Claims extractClaims(String token) {
        try {
            log.info(":::: public key path {}", publicKeyPath);
            PublicKey publicKey = KeyUtil.loadPublicKey(publicKeyPath);
            return JwtUtil.verify(token, publicKey);
        } catch (Exception e) {
            log.error("Cannot load public key", e);
            throw new RuntimeException(e);
        }
    }
}
