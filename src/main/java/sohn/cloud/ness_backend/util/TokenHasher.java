package sohn.cloud.ness_backend.util;

import java.security.MessageDigest;
import java.util.Base64;

public class TokenHasher {

    public static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}