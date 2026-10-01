package org.example.backendweride.platform.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.Parameter;

import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendweride.platform.iam.domain.services.AccountQueryService;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AccountResource;
import org.example.backendweride.platform.iam.interfaces.rest.transform.AccountResourceFromEntityAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Accounts Controller
 *
 * @summary Handles HTTP requests related to account retrieval.
 */
@RestController
@RequestMapping(value = "api/v1/accounts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Accounts", description = "Consult the account of the authenticated user.")
public class AccountsController {
    private final AccountQueryService accountQueryService;
    public AccountsController(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    /**
     * Get account by ID.
     * @param accountId The ID of the account to retrieve.
     * @return ResponseEntity containing the account resource or a not found status.
     */
    @GetMapping("/{accountId}")
    @Operation (summary = "Get account by ID", description = "Retrieve account details using the account ID. Only the owner of the account can query it.")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The account belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountResource> getAccountById(@Parameter(description = "Unique identifier of the account") @PathVariable Long accountId, Authentication authentication) {
        CurrentUser.requireSelf(authentication, accountId);
        var getAccountByIdQuery = new GetAccountByIdQuery(accountId);
        var account = accountQueryService.handle(getAccountByIdQuery);
        if(account.isEmpty()) return ResponseEntity.notFound().build();

        var userResource = AccountResourceFromEntityAssembler.toResourceFromEntity(account.get());
        return ResponseEntity.ok(userResource);
    }
}