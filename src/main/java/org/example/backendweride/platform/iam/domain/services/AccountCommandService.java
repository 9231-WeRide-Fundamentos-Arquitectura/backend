package org.example.backendweride.platform.iam.domain.services;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.commands.SignInCommand;
import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Account Command Service Interface
 *
 * @summary Defines operations for handling account-related commands.
 */
@Service
public interface AccountCommandService {
    Optional<Account> handle (SignUpCommand command);
    Optional<ImmutablePair<Account, String>> handle(SignInCommand command);
}
