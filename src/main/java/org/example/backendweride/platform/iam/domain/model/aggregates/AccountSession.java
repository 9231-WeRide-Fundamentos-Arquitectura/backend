package org.example.backendweride.platform.iam.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@org.hibernate.annotations.DynamicUpdate
@Table(name = "sessions", indexes = @Index(columnList = "account_id"))
@Getter
@NoArgsConstructor
public class AccountSession {
    @Id private String id;
    @Column(nullable = false) private Long accountId;
    @Column(nullable = false) private Instant createdAt;
    private Instant lastSeenAt;
    private Instant revokedAt;
    @Column(nullable = false) private Instant expiresAt;
    @Column(length = 500) private String userAgent;

    public AccountSession(Long accountId, Instant expiresAt, String userAgent) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.createdAt = Instant.now();
        this.lastSeenAt = createdAt;
        this.expiresAt = expiresAt;
        this.userAgent = userAgent == null ? null : userAgent.substring(0, Math.min(500, userAgent.length()));
    }

    public boolean isActive() { return revokedAt == null && expiresAt.isAfter(Instant.now()); }
    public void seen() { lastSeenAt = Instant.now(); }
    public void revoke() { revokedAt = Instant.now(); }
}
