package com.zwind.identityservice.utils;

import java.security.PublicKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtil {
    public static Claims verify(String token, PublicKey key){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
