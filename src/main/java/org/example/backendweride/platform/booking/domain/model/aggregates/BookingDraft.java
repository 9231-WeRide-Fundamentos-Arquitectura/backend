package org.example.backendweride.platform.booking.domain.model.aggregates;

import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingDraftCommand;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Private saved booking form. Server expiry is 24 hours after saving; the draft does not reserve or unlock the vehicle.")
@Entity
@Getter
@NoArgsConstructor
public class BookingDraft {
    @Schema(description = "Server-generated draft UUID")
    @Id private String id;
    @Schema(description = "Owner's account ID", example = "1")
    private String userId;
    @Schema(description = "Existing vehicle ID", example = "7")
    private String vehicleId;
    @Schema(description = "Saved local calendar date in YYYY-MM-DD format", example = "2026-10-02")
    private String selectedDate;
    @Schema(description = "Saved local time in HH:mm format", example = "14:30")
    private String unlockTime;
    @Schema(description = "Saved duration in hours", minimum = "0.25", maximum = "24", example = "1")
    private double duration;
    @Schema(description = "Saved SMS reminder preference; does not send an SMS")
    private boolean smsReminder;
    @Schema(description = "Saved email confirmation preference; does not send an email")
    private boolean emailConfirmation;
    @Schema(description = "Saved push notification preference; does not send a push notification")
    private boolean pushNotification;
    @Schema(description = "Server timestamp at which the draft was saved")
    private Instant savedAt;
    @Schema(description = "Server expiry timestamp, 24 hours after savedAt")
    private Instant expiresAt;

    public BookingDraft(CreateBookingDraftCommand command) {
        this.id = UUID.randomUUID().toString();
        this.userId = command.userId();
        this.vehicleId = command.vehicleId();
        this.selectedDate = command.selectedDate();
        this.unlockTime = command.unlockTime();
        this.duration = command.duration();
        this.smsReminder = command.smsReminder();
        this.emailConfirmation = command.emailConfirmation();
        this.pushNotification = command.pushNotification();
        this.savedAt = Instant.now();
        this.expiresAt = savedAt.plusSeconds(86400);
    }
}
