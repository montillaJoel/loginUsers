package src.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Password hashing and verification with PBKDF2 (HMAC-SHA256) and a random salt.
 * <p>
 * Hash keys are stored as {@code iterations:salt:hash}, with the salt and hash in Base64.
 */
public class SureKeys {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    /**
     * Hashes a password using PBKDF2.
     *
     * @param password   the password to hash
     * @param salt       the salt to use
     * @param iterations the number of PBKDF2 iterations
     * @param keyLength  the length of the resulting hash, in bits
     * @return the raw hash bytes
     * @throws NoSuchAlgorithmException if PBKDF2WithHmacSHA256 is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Hashes a password with a new random salt.
     *
     * @param password the plain-text password
     * @return the hash key in the format {@code iterations:salt:hash} (salt and hash in Base64)
     * @throws NoSuchAlgorithmException if PBKDF2WithHmacSHA256 is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    public static String generateHashKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
       SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        return ITERATIONS + ":" + saltBase64 + ":" + hashBase64;
    }

    /**
     * Verifies an entered password against a stored hash key.
     * <p>
     * Uses a constant-time comparison to avoid timing attacks.
     *
     * @param enteredPassword the plain-text password to check
     * @param savedHashKey    the stored hash key, as returned by {@link #generateHashKey(String)}
     * @return {@code true} if the password matches, {@code false} otherwise
     * @throws NoSuchAlgorithmException if PBKDF2WithHmacSHA256 is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
     public static boolean verifyPassword(String enteredPassword, String savedHashKey)
     throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = savedHashKey.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] savedHash = Base64.getDecoder().decode(parts[2]);

        byte[] testHash = pbkdf2(enteredPassword.toCharArray(), salt, iterations, savedHash.length * 8);

        return MessageDigest.isEqual(savedHash, testHash);
     }
}
