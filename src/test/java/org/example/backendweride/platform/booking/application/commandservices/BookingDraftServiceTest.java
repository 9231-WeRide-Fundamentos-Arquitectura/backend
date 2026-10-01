package org.example.backendweride.platform.booking.application.commandservices;

import org.example.backendweride.platform.booking.domain.model.aggregates.BookingDraft;
import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingDraftCommand;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingDraftRepository;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingDraftServiceTest {
    @Test
    void validatesDatesAssignsServerExpiryAndRestrictsOwnership() {
        var drafts = mock(BookingDraftRepository.class);
        var vehicles = mock(VehicleRepository.class);
        when(vehicles.existsById(1L)).thenReturn(true);
        when(drafts.save(any(BookingDraft.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var service = new BookingDraftService(drafts, vehicles);
        var before = Instant.now();
        var draft = service.handle(new CreateBookingDraftCommand("7", "1", "2026-10-02", "14:00", 1, false, false, true));
        assertFalse(draft.getSavedAt().isBefore(before));
        assertFalse(draft.getSavedAt().isAfter(Instant.now()));
        assertEquals(draft.getSavedAt().plusSeconds(86400), draft.getExpiresAt());
        when(drafts.findById(draft.getId())).thenReturn(Optional.of(draft));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.delete("8", draft.getId())).getStatusCode().value());
        verify(drafts, never()).delete(any(BookingDraft.class));
        service.delete("7", draft.getId());
        verify(drafts).delete(draft);
        clearInvocations(drafts, vehicles);
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.handle(
                new CreateBookingDraftCommand("7", "1", "2026-02-30", "14:00", 1, false, false, false))).getStatusCode().value());
        verifyNoInteractions(drafts, vehicles);
        when(drafts.findByUserIdAndExpiresAtAfterOrderBySavedAtDesc(eq("7"), any(Instant.class), any(Pageable.class))).thenReturn(List.of());
        assertEquals(List.of(), service.list("7", 0, 100));
        verify(drafts).findByUserIdAndExpiresAtAfterOrderBySavedAtDesc(eq("7"), argThat(time -> !time.isBefore(before)), any(Pageable.class));
    }
}
