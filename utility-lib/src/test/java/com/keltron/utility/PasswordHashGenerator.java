package com.keltron.utility;

import com.keltron.utility.security.PasswordHasher;

/**
 * Developer utility (test-scope) to generate BCrypt hashes locally.
 * Usage: run with args[0] = raw password.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        if (args == null || args.length == 0 || args[0] == null || args[0].isBlank()) {
            System.err.println("Usage: PasswordHashGenerator <rawPassword>");
            System.exit(2);
        }
        System.out.println("BCrypt Hash: " + PasswordHasher.bcrypt(args[0]));
    }
}

