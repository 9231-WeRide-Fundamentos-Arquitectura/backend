package org.example.backendweride.platform.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.iam.domain.services.AccountCommandService;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AccountResource;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AuthenticatedAccountResource;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignInResource;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignUpResource;
import org.example.backendweride.platform.iam.interfaces.rest.transform.AccountResourceFromEntityAssembler;
import org.example.backendweride.platform.iam.interfaces.rest.transform.AuthenticatedAccountResourceFromEntityAssembler;
import org.example.backendweride.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import org.example.backendweride.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Auth Controller
 *
 * @summary Handles authentication-related endpoints such as sign-in and sign-up.
 */
@RestController
@RequestMapping(value = "api/v1/authentication", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Manage Auth in the System")
public class IamAuthController {
    private final AccountCommandService accountCommandService;

    public IamAuthController(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }

    /**
     * Sign in to the system.
     * @param signInResource The sign-in details.
     * @return ResponseEntity containing the authenticated account resource or an error status.
     */
    @PostMapping("/sign-in")
    @Operation(summary = "Sign in to the system", description = "Authenticate a user and return an authenticated account resource.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "404", description = "Authentication failed, account not found")
    })
    public ResponseEntity<AuthenticatedAccountResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedAccountResult = accountCommandService.handle(signInCommand);

        if(authenticatedAccountResult.isEmpty()) return ResponseEntity.notFound().build();
        var authenticatedAccount = authenticatedAccountResult.get();
        var authenticatedAccountResource =
                AuthenticatedAccountResourceFromEntityAssembler.toResourceFromEntity(authenticatedAccount.left, authenticatedAccount.right);
        return ResponseEntity.ok(authenticatedAccountResource);
    }

    /**
     * Sign up for a new account.
     * @param signUpResource The sign-up details.
     * @return ResponseEntity containing the created account resource or an error status.
     */
    @PostMapping("/sign-up")
    @Operation(summary = "Sign up for a new account", description = "Create a new user account in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Account creation failed due to bad request")
    })
    public ResponseEntity<AccountResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var account = accountCommandService.handle(signUpCommand);

        if (account.isEmpty()) return ResponseEntity.badRequest().build();
        var accountEntity = account.get();
        var accountResource = AccountResourceFromEntityAssembler.toResourceFromEntity(accountEntity);
        return new ResponseEntity<>(accountResource, HttpStatus.CREATED);
    }
}