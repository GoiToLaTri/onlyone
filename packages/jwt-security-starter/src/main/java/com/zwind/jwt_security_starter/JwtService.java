package com.zwind.jwt_security_starter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtService {
    JwtProperties jwtProperties;
    ResourceLoader resourceLoader;

    public Claims extractClaims(String token) {
        try {
            String path = jwtProperties.getPublicKeyPath();
            Resource resource = resourceLoader.getResource(path);
            // log.info(":::: public key path {}", jwtProperties.getPublicKeyPath());
            
          try(InputStream inputStream = resource.getInputStream()) {
            PublicKey publicKey = loadPublicKeyFromStream(inputStream);
            return verify(token, publicKey);
          }
        } catch (Exception e) {
            log.error("Cannot load public key", e);
            throw new RuntimeException(e);
        }
    }

    private Claims verify(String token, PublicKey key){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    // private PublicKey loadPublicKey(String path) throws Exception {
    //     String key = new String(Files.readAllBytes(Paths.get(path)));
    //     log.info(":::: key: {}",key);
    //     key = key
    //             .replace("-----BEGIN PUBLIC KEY-----", "")
    //             .replace("-----END PUBLIC KEY-----", "")
    //             .replaceAll("\\s", "");
    //     byte[] decoded = Base64.getDecoder().decode(key);

    //     X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    //     KeyFactory kf = KeyFactory.getInstance("RSA");

    //     return kf.generatePublic(spec);
    // }

    private PublicKey loadPublicKeyFromStream(InputStream inputStream) throws Exception {
    // 1. Đọc toàn bộ nội dung từ InputStream thành String
    String keyContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

    // 2. Làm sạch chuỗi PEM:
    // - Loại bỏ Header: -----BEGIN PUBLIC KEY-----
    // - Loại bỏ Footer: -----END PUBLIC KEY-----
    // - Loại bỏ tất cả các ký tự xuống dòng (\n, \r) hoặc khoảng trắng
    String publicKeyPEM = keyContent
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", ""); // Loại bỏ tất cả khoảng trắng dư thừa

    // 3. Giải mã Base64 để lấy mảng byte
    byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

    // 4. Tạo đối tượng PublicKey thông qua X509EncodedKeySpec (chuẩn cho Public Key)
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    
    return keyFactory.generatePublic(keySpec);
}
}
