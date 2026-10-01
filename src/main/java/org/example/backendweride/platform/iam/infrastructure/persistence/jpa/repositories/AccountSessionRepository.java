package org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories;

import org.example.backendweride.platform.iam.domain.model.aggregates.AccountSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface AccountSessionRepository extends JpaRepository<AccountSession, String> {
    List<AccountSession> findByAccountIdAndRevokedAtIsNullAndExpiresAtAfter(Long accountId, Instant now);
    void deleteAllByAccountId(Long accountId);
}
