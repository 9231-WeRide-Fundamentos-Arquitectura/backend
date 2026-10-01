package org.example.backendweride.platform.booking.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Data required to reserve a vehicle. userId must match the authenticated account. startDate, endDate and totalCost are optional: omit them for an immediate ride.")
public record CreateBookingResource(
        String userId,
        String vehicleId,
        String startLocationId,
        String endLocationId,
        Date startDate,
        Date endDate,
        Double totalCost
) {
}