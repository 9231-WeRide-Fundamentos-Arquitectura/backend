package org.example.backendweride.platform.iam.application.internal.outboundservices.tokens;

/**
 * Token Service Interface
 *
 * @summary Defines operations for generating and validating tokens.
 */
public interface TokenService {

    String generateToken(String username);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);
}
