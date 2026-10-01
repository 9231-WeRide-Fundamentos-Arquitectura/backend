package org.example.backendweride.platform.iam.application.internal.queryservices;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendweride.platform.iam.domain.services.AccountQueryService;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of AccountQueryService to handle account-related queries.
 *
 * @summary This service retrieves account information based on provided queries.
 */
@Service
public class AccountQueryServiceImpl implements AccountQueryService {
    private final AccountRepository accountRepository;

    public AccountQueryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> handle(GetAccountByIdQuery query) {
        var accountFound = accountRepository.findById(query.id());
        if(accountFound.isEmpty()) throw new IllegalArgumentException("Account not found");
        return accountFound;
    }
}

