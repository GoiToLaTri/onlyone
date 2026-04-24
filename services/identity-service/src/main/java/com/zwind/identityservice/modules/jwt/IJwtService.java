package com.zwind.identityservice.modules.jwt;

import io.jsonwebtoken.Claims;

public interface IJwtService {
   Claims extractClaims(String token);
}
