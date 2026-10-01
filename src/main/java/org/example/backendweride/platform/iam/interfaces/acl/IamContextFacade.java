package org.example.backendweride.platform.iam.interfaces.acl;

import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendweride.platform.iam.domain.services.AccountCommandService;
import org.example.backendweride.platform.iam.domain.services.AccountQueryService;
import org.springframework.stereotype.Service;

/*
 * IamContextFacade
 * This class serves as a facade for IAM context operations, providing simplified methods for account creation and retrieval.
 *
 * @summary Facade for IAM context operations.
*/

@Service
public class IamContextFacade {
    private final AccountCommandService accountCommandService;
    private final AccountQueryService accountQueryService;

    public IamContextFacade(AccountCommandService accountCommandService,
                            AccountQueryService accountQueryService)
    {
        this.accountCommandService = accountCommandService;
        this.accountQueryService = accountQueryService;
    }

    public Long createAccount(String username, String password) {
        var signUpCommand = new SignUpCommand(username, password);
        var result = accountCommandService.handle(signUpCommand);
        if (result.isEmpty())
            throw new RuntimeException("Error creating account");
        return result.get().getId();
    }

    public String fetchUsernameByAccountId(Long accountId) {
        var getAccountByIdQuery = new GetAccountByIdQuery(accountId);
        var result = accountQueryService.handle(getAccountByIdQuery);
        if (result.isEmpty())
            throw new RuntimeException("Account not found");
        return result.get().getUserName();
    }
}
