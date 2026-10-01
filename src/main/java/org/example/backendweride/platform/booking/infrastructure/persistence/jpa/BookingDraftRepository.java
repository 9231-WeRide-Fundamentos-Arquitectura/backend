package org.example.backendweride.platform.booking.infrastructure.persistence.jpa;

import org.example.backendweride.platform.booking.domain.model.aggregates.BookingDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;

public interface BookingDraftRepository extends JpaRepository<BookingDraft, String> {
    long countByUserIdAndExpiresAtAfter(String userId, Instant now);
    List<BookingDraft> findByUserIdAndExpiresAtAfterOrderBySavedAtDesc(String userId, Instant now, Pageable page);
}
