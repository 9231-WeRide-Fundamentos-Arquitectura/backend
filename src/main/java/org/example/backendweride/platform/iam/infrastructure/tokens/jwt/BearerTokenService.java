package org.example.backendweride.platform.iam.infrastructure.tokens.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.example.backendweride.platform.iam.application.internal.outboundservices.tokens.TokenService;

/**
 * BearerTokenService interface that extends TokenService.
 *
 * @summary This interface provides methods for extracting bearer tokens from HTTP requests
 */
public interface BearerTokenService extends TokenService {
String getBearerTokenFrom(HttpServletRequest token);

String generateToken(Authentication authentication);
}
