package com.keltron.utility.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Centralized password hashing utility for consistent BCrypt usage across DTO/entity layers.
 */
public final class PasswordHasher {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordHasher() {
    }

    public static String bcrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return null;
        }
        return ENCODER.encode(rawPassword);
    }
}

