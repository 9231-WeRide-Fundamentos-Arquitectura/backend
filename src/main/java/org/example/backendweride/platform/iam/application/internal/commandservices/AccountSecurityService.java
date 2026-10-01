package org.example.backendweride.platform.iam.application.internal.commandservices;

import jakarta.persistence.EntityManager;
import org.example.backendweride.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountSessionRepository;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AccountSecurityService {
    private final AccountRepository accounts;
    private final AccountSessionRepository sessions;
    private final BookingRepository bookings;
    private final HashingService hashing;
    private final EntityManager entities;

    public AccountSecurityService(AccountRepository accounts, AccountSessionRepository sessions, BookingRepository bookings, HashingService hashing, EntityManager entities) {
        this.accounts = accounts;
        this.sessions = sessions;
        this.bookings = bookings;
        this.hashing = hashing;
        this.entities = entities;
    }

    private Account account(Long id, String sid) {
        var account = accounts.findLockedById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var current = sid == null ? null : sessions.findById(sid).orElse(null);
        if (current == null || !current.isActive() || !id.equals(current.getAccountId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return account;
    }

    private void checkPassword(Account account, String password) {
        if (password == null || !hashing.matches(password, account.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CURRENT_PASSWORD_INVALID");
    }

    private int revoke(Long id, String except) {
        var others = sessions.findByAccountIdAndRevokedAtIsNullAndExpiresAtAfter(id, Instant.now());
        int count = 0;
        for (var session : others) {
            if (!session.getId().equals(except)) { session.revoke(); count++; }
        }
        sessions.saveAll(others);
        return count;
    }

    public void changePassword(Long id, String sid, String currentPassword, String newPassword) {
        var account = account(id, sid);
        checkPassword(account, currentPassword);
        if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 64 || newPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72 || newPassword.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NEW_PASSWORD_INVALID");
        account.updatePassword(hashing.encode(newPassword));
        accounts.save(account);
        revoke(id, sid);
    }

    public int revokeOthers(Long id, String sid) {
        account(id, sid);
        return revoke(id, sid);
    }

    public void logout(Long id, String sid) {
        account(id, sid);
        var session = sessions.findById(sid).orElseThrow();
        session.revoke();
        sessions.save(session);
    }

    public void deleteAccount(Long id, String sid, String password) {
        var account = account(id, sid);
        checkPassword(account, password);
        var ownBookings = bookings.findAllByUserId(id.toString());
        if (ownBookings.stream().anyMatch(booking -> java.util.List.of("reserved", "in_progress", "paused").contains(booking.getStatus())))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ACTIVE_BOOKINGS");
        ownBookings.forEach(booking -> booking.anonymize("deleted-" + UUID.randomUUID()));
        bookings.saveAll(ownBookings);
        // Remove entities individually so JPA also removes element collections such as report photos/categories.
        for (String entity : java.util.List.of("BookingDraft", "UnlockRequest", "ProblemReport", "Trip"))
            entities.createQuery("select e from " + entity + " e where e.userId = :id").setParameter("id", id.toString()).getResultList().forEach(entities::remove);
        for (String entity : java.util.List.of("Favorite", "ProfileEntity", "TravelHistory"))
            entities.createQuery("select e from " + entity + " e where e.userId = :id").setParameter("id", id).getResultList().forEach(entities::remove);
        entities.createQuery("select e from Notification e where e.userId = :id").setParameter("id", account.getUserName()).getResultList().forEach(entities::remove);
        sessions.deleteAllByAccountId(id);
        accounts.delete(account);
    }
}
