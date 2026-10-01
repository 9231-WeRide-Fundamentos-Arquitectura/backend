package org.example.backendweride.platform.iam.infrastructure.auth.model;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Builder for creating UsernamePasswordAuthenticationToken instances.
 *
 * @summary Utility class for building authentication tokens
 */
public class UsernamePasswordAuthTokenBuilder {
    public static UsernamePasswordAuthenticationToken build(UserDetails principal, HttpServletRequest request){
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }
}
