package org.example.backendweride.platform.booking.domain.model.commands;

import java.util.Date;

public record CreateBookingCommand(
        String userId,
        String vehicleId,
        String startLocationId,
        String endLocationId,
        Date startDate,
        Date endDate,
        Double totalCost
) {
}