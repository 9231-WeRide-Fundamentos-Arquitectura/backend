package org.example.backendweride.platform.iam.infrastructure.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of UserDetails to represent authenticated user information.
 *
 * @summary UserDetails implementation for authenticated users
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private final String username;
    @JsonIgnore
    private final String password;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Account account) {
        // For now, WeRide only handles the CLIENT role
        // All authenticated users have this single role
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));

        return new UserDetailsImpl(
                account.getUserName(),
                account.getPassword(),
                authorities
        );
    }

}
