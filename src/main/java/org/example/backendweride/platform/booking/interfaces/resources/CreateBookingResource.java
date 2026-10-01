package org.example.backendweride.platform.booking.interfaces.resources;

public record CreateBookingResource(
        String userId,
        String vehicleId,
        String startLocationId,
        String endLocationId
) {
}