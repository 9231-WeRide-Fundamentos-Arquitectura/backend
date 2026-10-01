package org.example.backendweride.platform.iam;

import jakarta.persistence.EntityManager;
import org.example.backendweride.platform.iam.application.internal.commandservices.AccountSecurityService;
import org.example.backendweride.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.aggregates.AccountSession;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountSessionRepository;
import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountSecurityTest {
    @Test
    void passwordChangeChecksCurrentPasswordAndRevokesOnlyOtherUnexpiredSessions() {
        var accounts = mock(AccountRepository.class);
        var sessions = mock(AccountSessionRepository.class);
        var bookings = mock(BookingRepository.class);
        var hashing = mock(HashingService.class);
        var entities = mock(EntityManager.class);
        var account = new Account("test@example.com", "hash");
        var current = new AccountSession(7L, Instant.now().plusSeconds(3600), null);
        var other = new AccountSession(7L, Instant.now().plusSeconds(3600), null);
        when(accounts.findLockedById(7L)).thenReturn(Optional.of(account));
        when(sessions.findById(current.getId())).thenReturn(Optional.of(current));
        when(sessions.findByAccountIdAndRevokedAtIsNullAndExpiresAtAfter(eq(7L), any())).thenReturn(List.of(current, other));
        var security = new AccountSecurityService(accounts, sessions, bookings, hashing, entities);
        var error = assertThrows(ResponseStatusException.class, () -> security.changePassword(7L, current.getId(), "wrong", "new-password"));
        assertEquals("CURRENT_PASSWORD_INVALID", error.getReason());
        verify(accounts, never()).save(any());
        assertTrue(other.isActive());
        when(hashing.matches("correct", "hash")).thenReturn(true);
        when(hashing.encode("new-password")).thenReturn("new-hash");
        security.changePassword(7L, current.getId(), "correct", "new-password");
        assertEquals("new-hash", account.getPassword());
        assertTrue(current.isActive());
        assertFalse(other.isActive());
        when(sessions.findByAccountIdAndRevokedAtIsNullAndExpiresAtAfter(eq(7L), any())).thenReturn(List.of(current));
        assertEquals(0, security.revokeOthers(7L, current.getId()));
        var active = new Booking(new CreateBookingCommand("7", "1", "1", "1", null, null, null));
        when(bookings.findAllByUserId("7")).thenReturn(List.of(active));
        when(hashing.matches("correct", "new-hash")).thenReturn(true);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class, () -> security.deleteAccount(7L, current.getId(), "correct")).getStatusCode());
        verify(accounts, never()).delete(any());
        verifyNoInteractions(entities);
        active.cancel();
        active.anonymize("deleted-random");
        assertEquals("deleted-random", active.getUserId());
        assertNull(active.getRating());
        assertTrue(active.getRouteCoordinates().isEmpty());
        var query = mock(jakarta.persistence.Query.class);
        when(entities.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());
        security.deleteAccount(7L, current.getId(), "correct");
        verify(accounts).delete(account);
        verify(sessions).deleteAllByAccountId(7L);
        verify(entities, times(8)).createQuery(anyString());
    }
}
