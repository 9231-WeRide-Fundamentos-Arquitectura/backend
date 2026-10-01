package org.example.backendweride.platform.booking.application.commandservices;

import org.example.backendweride.platform.booking.domain.model.aggregates.BookingDraft;
import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingDraftCommand;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingDraftRepository;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingDraftService {
    private static final int MAX_ACTIVE_DRAFTS = 20;
    private final BookingDraftRepository drafts;
    private final VehicleRepository vehicles;
    public BookingDraftService(BookingDraftRepository drafts, VehicleRepository vehicles) {
        this.drafts = drafts;
        this.vehicles = vehicles;
    }
    public List<BookingDraft> list(String userId, int page, int limit) {
        if (page < 0 || limit < 1 || limit > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page");
        return drafts.findByUserIdAndExpiresAtAfterOrderBySavedAtDesc(userId, Instant.now(), PageRequest.of(page, limit));
    }
    @Transactional
    public BookingDraft handle(CreateBookingDraftCommand command) {
        try {
            LocalDate.parse(command.selectedDate());
            LocalTime.parse(command.unlockTime());
            if (!Double.isFinite(command.duration()) || command.duration() < 0.25 || command.duration() > 24)
                throw new IllegalArgumentException();
        } catch (java.time.DateTimeException | IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date, time or duration");
        }
        long vehicleId;
        try { vehicleId = Long.parseLong(command.vehicleId()); }
        catch (RuntimeException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vehicle id"); }
        if (!vehicles.existsById(vehicleId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");
        if (drafts.countByUserIdAndExpiresAtAfter(command.userId(), Instant.now()) >= MAX_ACTIVE_DRAFTS)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Too many active drafts; delete one first");
        return drafts.save(new BookingDraft(command));
    }
    @Transactional
    public void delete(String userId, String id) {
        var draft = drafts.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!userId.equals(draft.getUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Draft belongs to another user");
        drafts.delete(draft);
    }
}
