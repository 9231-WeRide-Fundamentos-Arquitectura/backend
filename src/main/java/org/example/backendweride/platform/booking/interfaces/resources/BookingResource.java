package org.example.backendweride.platform.booking.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "A booking of a vehicle made by a user, with its status, costs, payment and ride metrics.")
public record BookingResource(
        String id,
        String userId,
        String vehicleId,
        String startLocationId,
        String endLocationId,
        Date reservedAt,
        Date startDate,
        Date endDate,
        Date actualStartDate,
        Date actualEndDate,
        String status,
        Double totalCost,
        Double discount,
        Double finalCost,
        String paymentMethod,
        String paymentStatus,
        Double distance,
        Integer duration,
        Double averageSpeed,
        RatingResource rating
) {
    public record RatingResource(Integer score, String comment) {}
}