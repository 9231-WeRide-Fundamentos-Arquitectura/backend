package org.example.backendweride.platform.booking.domain.model.commands;

import java.util.List;
import org.example.backendweride.platform.booking.domain.model.valueobjects.TripRouteCoordinate;

public record CompleteBookingCommand(
        String bookingId,
        Double totalCost,
        Double discount,
        Double distance,
        Integer duration,
        Double averageSpeed,
        Integer ratingScore,
        String ratingComment,
        List<TripRouteCoordinate> routeCoordinates,
        String routeSource
) {
}