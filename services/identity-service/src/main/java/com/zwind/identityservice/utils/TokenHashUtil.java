package com.zwind.identityservice.utils;

import java.security.MessageDigest;

public class TokenHashUtil {
    public static String sha256(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for(byte b: hash)
                hex.append(String.format("%02x", b));

            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
