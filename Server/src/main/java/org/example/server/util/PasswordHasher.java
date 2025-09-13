package org.example.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

  // Hashes a password using SHA-512 with a random salt and return Base64-encoded string of the
  // hashed password.
  public static String hashPassword(String password, String salt) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(Base64.getDecoder().decode(salt));
      byte[] hashedPassword = md.digest(password.getBytes());
      return Base64.getEncoder().encodeToString(hashedPassword);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-512 algorithm not found.", e);
    }
  }

  // Generates a new random salt.
  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  // Checks a plain-text password against a stored hash and salt and return true if the password
  // matches, false otherwise.
  public static boolean checkPassword(String password, String storedHash, String storedSalt) {
    String hashedPassword = hashPassword(password, storedSalt);
    return hashedPassword.equals(storedHash);
  }
}
