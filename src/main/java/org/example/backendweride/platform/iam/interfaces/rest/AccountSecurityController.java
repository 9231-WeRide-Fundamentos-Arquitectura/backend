package org.example.backendweride.platform.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.example.backendweride.platform.iam.application.internal.commandservices.AccountSecurityService;
import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.example.backendweride.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts/me")
@Tag(name = "Account security")
public class AccountSecurityController {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Current password and replacement password; never persisted as plain text")
    public record PasswordChange(@NotBlank String currentPassword, @NotBlank String newPassword) {}
    @io.swagger.v3.oas.annotations.media.Schema(description = "Password confirmation for permanent account deletion")
    public record DeleteAccount(@NotBlank String password) {}
    private final AccountSecurityService security;
    private final BearerTokenService tokens;

    public AccountSecurityController(AccountSecurityService security, BearerTokenService tokens) {
        this.security = security;
        this.tokens = tokens;
    }

    private String sid(HttpServletRequest request) { return tokens.getSessionIdFromToken(tokens.getBearerTokenFrom(request)); }

    @PutMapping("/password")
    @Operation(summary = "Change password and revoke other devices", description = "Requires the current password. The new password must contain 8 to 64 characters and fit the BCrypt 72-byte limit. Keeps the current session.")
    @ApiResponse(responseCode = "400", description = "CURRENT_PASSWORD_INVALID or NEW_PASSWORD_INVALID")
    public ResponseEntity<Void> password(@Valid @RequestBody PasswordChange body, Authentication authentication, HttpServletRequest request) {
        security.changePassword(CurrentUser.id(authentication), sid(request), body.currentPassword(), body.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sessions/revoke-others")
    @Operation(summary = "Revoke other active sessions", description = "Returns the number revoked, including zero when no other unexpired sessions exist.")
    public Map<String, Integer> revoke(Authentication authentication, HttpServletRequest request) {
        return Map.of("revoked", security.revokeOthers(CurrentUser.id(authentication), sid(request)));
    }

    @PostMapping("/sessions/logout")
    @Operation(summary = "Revoke the current session")
    public ResponseEntity<Void> logout(Authentication authentication, HttpServletRequest request) {
        security.logout(CurrentUser.id(authentication), sid(request));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete")
    @Operation(summary = "Delete own account", description = "Requires the password and no active bookings. Removes profile, favorites, drafts, reports, notifications and sessions. Retains anonymous fleet history without ratings or GPS routes.")
    @ApiResponse(responseCode = "409", description = "ACTIVE_BOOKINGS: finish or cancel all active bookings first")
    @ApiResponse(responseCode = "400", description = "CURRENT_PASSWORD_INVALID")
    public ResponseEntity<Void> delete(@Valid @RequestBody DeleteAccount body, Authentication authentication, HttpServletRequest request) {
        security.deleteAccount(CurrentUser.id(authentication), sid(request), body.password());
        return ResponseEntity.noContent().build();
    }
}
