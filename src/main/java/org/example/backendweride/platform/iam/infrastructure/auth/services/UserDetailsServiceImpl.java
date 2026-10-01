package org.example.backendweride.platform.iam.infrastructure.auth.services;

import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserDetailsService to load user details from the database.
 *
 * @summary This class implements the UserDetailsService interface to provide user authentication details.
 */
@Service
@Qualifier("defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = accountRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User Not Found with username: " + username));
        return org.example.backendweride.platform.iam.infrastructure.auth.model.UserDetailsImpl.build(user);
    }
}
