package org.example.backendweride.platform.problemreports.application.internal.commandservices;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.problemreports.domain.model.aggregates.ProblemReport;
import org.example.backendweride.platform.problemreports.domain.model.commands.CreateProblemReportCommand;
import org.example.backendweride.platform.problemreports.infrastructure.persistence.jpa.ProblemReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
import java.util.Set;

@Service
public class ProblemReportCommandService {
    private static final Set<String> CATEGORIES = Set.of("mechanical", "tires", "gps", "battery", "lock", "other", "brakes", "damage");
    private static final long RECENT_COMPLETED_MS = 3_600_000L;
    private final EntityManager entities;
    private final ProblemReportRepository reports;
    public ProblemReportCommandService(EntityManager entities, ProblemReportRepository reports) {
        this.entities = entities;
        this.reports = reports;
    }

    @Transactional
    public ProblemReport handle(CreateProblemReportCommand command) {
        if (command.categories() == null || command.categories().isEmpty() || command.categories().size() > CATEGORIES.size()
                || command.categories().stream().anyMatch(java.util.Objects::isNull)
                || !CATEGORIES.containsAll(command.categories()) || command.description() == null || command.description().length() > 2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid problem categories or description");
        validatePhoto(command.photo());
        long vehicleId;
        try { vehicleId = Long.parseLong(command.vehicleId()); }
        catch (RuntimeException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vehicle id"); }
        var vehicle = entities.find(Vehicle.class, vehicleId, LockModeType.PESSIMISTIC_WRITE);
        if (vehicle == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");
        boolean waived = false;
        // Only someone holding a booking of this vehicle may take it out of the fleet.
        if (command.bookingId() == null || command.bookingId().isBlank())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A booking of this vehicle is required to report a problem");
        var booking = entities.find(Booking.class, command.bookingId(), LockModeType.PESSIMISTIC_WRITE);
        if (booking == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        if (!command.userId().equals(booking.getUserId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking belongs to another user");
        if (!command.vehicleId().equals(booking.getVehicleId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle does not match booking");
        boolean recentlyCompleted = "completed".equals(booking.getStatus()) && booking.getActualEndDate() != null
                && System.currentTimeMillis() - booking.getActualEndDate().getTime() <= RECENT_COMPLETED_MS;
        if (!Booking.ACTIVE_STATUSES.contains(booking.getStatus()) && !recentlyCompleted)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking is not active or recently completed");
        long elapsed = booking.getActualStartDate() == null ? -1 : System.currentTimeMillis() - booking.getActualStartDate().getTime();
        waived = "in_progress".equals(booking.getStatus()) && elapsed >= 0 && elapsed <= 60_000;
        if (waived) {
            booking.completeBooking(0.0, 0.0, 0.0, (int)(elapsed / 60_000), 0.0, null);
            booking.markChargeWaived();
        }
        vehicle.markForMaintenance();
        // The pending_maintenance record is the operational queue; external maintenance notifications need a separate integration.
        return reports.save(new ProblemReport(command.userId(), command.vehicleId(), command.bookingId(),
                command.categories(), command.description().trim(), command.photo(), waived));
    }

    static void validatePhoto(String photo) {
        if (photo == null || photo.isBlank()) return;
        if (photo.length() > 1_400_000 || !photo.matches("^data:image/(jpeg|png|webp);base64,[A-Za-z0-9+/=]+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo must be JPEG, PNG or WebP up to 1 MB");
        try {
            byte[] bytes = Base64.getDecoder().decode(photo.substring(photo.indexOf(',') + 1));
            boolean jpeg = bytes.length > 3 && bytes[0] == (byte)0xff && bytes[1] == (byte)0xd8 && bytes[2] == (byte)0xff;
            boolean png = bytes.length > 8 && bytes[0] == (byte)0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e && bytes[3] == 0x47
                    && bytes[4] == 13 && bytes[5] == 10 && bytes[6] == 26 && bytes[7] == 10;
            boolean webp = bytes.length > 12 && bytes[0] == 82 && bytes[1] == 73 && bytes[2] == 70 && bytes[3] == 70
                    && bytes[8] == 87 && bytes[9] == 69 && bytes[10] == 66 && bytes[11] == 80;
            if (bytes.length > 1_048_576 || !(photo.startsWith("data:image/jpeg;") && jpeg || photo.startsWith("data:image/png;") && png
                    || photo.startsWith("data:image/webp;") && webp)) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image");
        }
    }
}
