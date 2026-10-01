package org.example.backendweride.platform.iam.interfaces.rest.transform;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AccountResource;

/**
 * Assembler class to convert Account entity to AccountResource.
 *
 * @summary This class provides a method to transform an Account entity
 *         into an AccountResource for REST API responses.
 */
public class AccountResourceFromEntityAssembler {
    public static AccountResource toResourceFromEntity(Account entity){
        return new AccountResource(entity.getId(), entity.getUserName());
    }
}
