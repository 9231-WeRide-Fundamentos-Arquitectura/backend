package org.example.backendweride.platform.booking.interfaces.resources;

import java.util.List;
import org.example.backendweride.platform.booking.domain.model.valueobjects.TripRouteCoordinate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Final ride data used to complete a booking: costs, distance, duration, average speed and optional rating.")
public record CompleteBookingResource(
        Double totalCost,
        Double discount,
        Double distance,
        Integer duration,
        Double averageSpeed,
        RatingResource rating,
        @Schema(description = "Optional observed route, up to 5000 valid GPS coordinates; omitted means no route was recorded.") List<TripRouteCoordinate> routeCoordinates,
        @Schema(description = "Origin of the route: gps (default) or simulated when it was interpolated between stations.", allowableValues = {"gps", "simulated"}) String routeSource
) {
    public record RatingResource(Integer score, String comment) {}
}
