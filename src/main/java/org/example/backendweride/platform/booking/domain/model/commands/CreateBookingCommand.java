package org.example.backendweride.platform.booking.domain.model.commands;

public record CreateBookingCommand(
        String userId,
        String vehicleId,
        String startLocationId,
        String endLocationId
) {
}