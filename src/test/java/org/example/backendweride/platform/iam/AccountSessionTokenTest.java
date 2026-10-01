package org.example.backendweride.platform.iam;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.aggregates.AccountSession;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountSessionRepository;
import org.example.backendweride.platform.iam.infrastructure.tokens.jwt.services.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountSessionTokenTest {
    @Test
    void issuedTokensHaveDistinctSessionsAndRevokedTokensFailValidation() {
        var accounts = mock(AccountRepository.class);
        var sessions = mock(AccountSessionRepository.class);
        var account = mock(Account.class);
        when(account.getId()).thenReturn(7L);
        when(account.getUserName()).thenReturn("test@example.com");
        when(accounts.findLockedByUserName("test@example.com")).thenReturn(Optional.of(account));
        when(accounts.findById(7L)).thenReturn(Optional.of(account));
        var saved = new HashMap<String, AccountSession>();
        when(sessions.save(any(AccountSession.class))).thenAnswer(invocation -> {
            AccountSession session = invocation.getArgument(0);
            saved.put(session.getId(), session);
            return session;
        });
        when(sessions.findById(anyString())).thenAnswer(invocation -> Optional.ofNullable(saved.get(invocation.getArgument(0))));
        var tokens = new TokenServiceImpl(accounts, sessions);
        ReflectionTestUtils.setField(tokens, "secret", UUID.randomUUID().toString() + UUID.randomUUID());
        ReflectionTestUtils.setField(tokens, "expirationDays", 1);
        var first = tokens.generateToken("test@example.com");
        var second = tokens.generateToken("test@example.com");
        assertNotEquals(tokens.getSessionIdFromToken(first), tokens.getSessionIdFromToken(second));
        assertTrue(tokens.validateToken(first));
        saved.get(tokens.getSessionIdFromToken(first)).revoke();
        assertFalse(tokens.validateToken(first));
        assertTrue(tokens.validateToken(second));
        saved.clear();
        assertFalse(tokens.validateToken(second));
    }
}
