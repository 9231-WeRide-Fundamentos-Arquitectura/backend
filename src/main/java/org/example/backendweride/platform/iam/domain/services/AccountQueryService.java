package org.example.backendweride.platform.iam.domain.services;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Account Query Service Interface
 *
 * @summary Defines operations for handling account-related queries.
 */
@Service
public interface AccountQueryService {
    Optional<Account> handle (GetAccountByIdQuery query);
}
