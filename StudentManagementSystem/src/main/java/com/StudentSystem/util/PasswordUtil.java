package com.StudentSystem.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtil {

    /**
     * Hashes a password using PBKDF2 with HMAC SHA-256 algorithm.
     * Generates a random 16-byte salt and applies 65,536 iterations.
     * Returns a string in the format: base64(salt):base64(hash).
     * 
     * @param password the plain text password to hash
     * @return the salted and hashed password string for storage
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16-byte random salt
        random.nextBytes(salt);

        // Create PBKDF2 specification with given password, salt, iterations, and key length
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);

        // Instantiate secret key factory for PBKDF2WithHmacSHA256
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        // Generate the hash
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Return salt and hash concatenated with ":" separator, both Base64 encoded
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }
    
    /**
     * Verifies a password against the stored salted hash.
     * Splits stored hash into salt and hash, then hashes input password with same salt and compares securely.
     * 
     * @param password   the plain text password to verify
     * @param storedHash the stored salted hash in format base64(salt):base64(hash)
     * @return true if password matches the stored hash, false otherwise
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static boolean verifyPassword(String password, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedHash.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]); // Decode salt
        byte[] hash = Base64.getDecoder().decode(parts[1]); // Decode stored hash

        // Hash input password using the extracted salt
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] testHash = factory.generateSecret(spec).getEncoded();

        // Compare hashes in constant time to prevent timing attacks
        return slowEquals(hash, testHash);
    }
    
    /**
     * Compares two byte arrays in length-constant time to prevent timing attacks.
     * 
     * @param a first byte array
     * @param b second byte array
     * @return true if arrays are equal, false otherwise
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length; // XOR length differences
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i]; // XOR each byte and OR into diff
        return diff == 0;
    }
}
