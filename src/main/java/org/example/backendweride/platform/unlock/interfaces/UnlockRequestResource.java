package org.example.backendweride.platform.unlock.interfaces;

import org.example.backendweride.platform.unlock.domain.model.UnlockRequest;
import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Persisted simulated unlock state, with server timestamps and attempt count. A failed code can be retried.")
public record UnlockRequestResource(
        @Schema(description = "Unlock request UUID") String id,
        @Schema(description = "Owner's account ID", example = "1") String userId,
        @Schema(description = "Vehicle ID matching the booking", example = "7") String vehicleId,
        @Schema(description = "Owned booking UUID") String bookingId,
        @Schema(description = "Server timestamp at which the request was created") Instant requestedAt,
        @Schema(description = "Booking's scheduled start, or the creation time for an immediate booking") Instant scheduledUnlockTime,
        @Schema(description = "Server timestamp of successful unlock; null until unlocked", nullable = true) Instant actualUnlockTime,
        @Schema(description = "Current unlock state", allowableValues = {"pending", "unlocked", "failed"}) String status,
        @Schema(description = "Most recently used unlock method", allowableValues = {"manual", "qr_code"}) String method,
        @Schema(description = "Compatibility field; always null because no location telemetry is collected", nullable = true) Object location,
        @Schema(description = "Code submitted on the latest attempt; empty before the first attempt", example = "weride:vehicle:7") String unlockCode,
        @Schema(description = "Number of processed code attempts", minimum = "0", example = "1") int attempts,
        @Schema(description = "Reason for a failed code; null when no code error exists", nullable = true) String errorMessage) {
    public static UnlockRequestResource from(UnlockRequest request) {
        return new UnlockRequestResource(request.getId(), request.getUserId(), request.getVehicleId(),
                request.getBookingId(), request.getRequestedAt(), request.getScheduledUnlockTime(),
                request.getActualUnlockTime(), request.getStatus(), request.getMethod(), null,
                request.getUnlockCode(), request.getAttempts(), request.getErrorMessage());
    }
}
