package org.example.backendweride.platform.booking.interfaces.resources;

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