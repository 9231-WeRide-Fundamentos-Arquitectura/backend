package org.example.backendweride.platform.booking.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data required to reserve a vehicle. userId must match the authenticated account.")
public record CreateBookingResource(
        String userId,
        String vehicleId,
        String startLocationId,
        String endLocationId
) {
}