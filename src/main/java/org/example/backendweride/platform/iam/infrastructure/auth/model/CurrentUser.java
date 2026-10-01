package org.example.backendweride.platform.iam.infrastructure.auth.model;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

/**
 * Ownership checks against the authenticated account (the JWT subject is the username,
 * resource owners are stored by account id).
 */
public final class CurrentUser {
    private CurrentUser() {}

    public static Long id(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl user))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return user.getId();
    }

    public static void requireSelf(Authentication authentication, Long ownerId) {
        if (!id(authentication).equals(ownerId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resource belongs to another user");
    }

    public static void requireSelf(Authentication authentication, String ownerId) {
        if (ownerId == null || !String.valueOf(id(authentication)).equals(ownerId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resource belongs to another user");
    }

    /** Notifications are keyed by username (JWT subject), not account id. */
    public static void requireUsername(Authentication authentication, String ownerUsername) {
        id(authentication);
        if (!authentication.getName().equals(ownerUsername))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resource belongs to another user");
    }
}
