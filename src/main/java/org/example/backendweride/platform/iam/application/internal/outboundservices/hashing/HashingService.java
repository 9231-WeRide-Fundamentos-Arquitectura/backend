package org.example.backendweride.platform.iam.application.internal.outboundservices.hashing;

/**
 * Hashing Service Interface
 *
 * @summary Defines operations for hashing and verifying passwords.
 */
public interface HashingService {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
