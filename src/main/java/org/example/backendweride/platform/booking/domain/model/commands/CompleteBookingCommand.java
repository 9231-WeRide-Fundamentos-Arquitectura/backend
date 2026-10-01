package org.example.backendweride.platform.booking.domain.model.commands;

public record CompleteBookingCommand(
        String bookingId,
        Double totalCost,
        Double discount,
        Double distance,
        Integer duration,
        Double averageSpeed,
        Integer ratingScore,
        String ratingComment
) {
}