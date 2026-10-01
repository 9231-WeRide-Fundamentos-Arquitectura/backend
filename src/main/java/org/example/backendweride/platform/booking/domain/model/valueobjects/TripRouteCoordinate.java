package org.example.backendweride.platform.booking.domain.model.valueobjects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Schema(description = "A GPS observation recorded during the ride; latitude [-90,90], longitude [-180,180].")
public record TripRouteCoordinate(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) Double lat,
                                  @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Double lng) {
    public TripRouteCoordinate {
        if (lat == null || lng == null || !Double.isFinite(lat) || !Double.isFinite(lng)
                || lat < -90 || lat > 90 || lng < -180 || lng > 180)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid route coordinates");
    }
}
