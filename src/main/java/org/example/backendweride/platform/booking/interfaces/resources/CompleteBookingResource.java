package org.example.backendweride.platform.booking.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Final ride data used to complete a booking: costs, distance, duration, average speed and optional rating.")
public record CompleteBookingResource(
        Double totalCost,
        Double discount,
        Double distance,
        Integer duration,
        Double averageSpeed,
        RatingResource rating
) {
    public record RatingResource(Integer score, String comment) {}
}