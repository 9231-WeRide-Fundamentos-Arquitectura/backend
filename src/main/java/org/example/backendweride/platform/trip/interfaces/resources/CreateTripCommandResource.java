package org.example.backendweride.platform.trip.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import org.example.backendweride.platform.trip.domain.valueobjects.RouteCoordinates;

import java.util.Date;
import java.util.List;

@Schema(description = "Data required to record a trip. userId must match the authenticated account.")
public record CreateTripCommandResource (
        long bookingId,
        String userId,
        Long vehicleId,
        Long startLocationId,
        Long endLocationId,
        String route,
        List<RouteCoordinates> routeCoordinates,
        Date startDate,
        Date endDate,
        int duration,
        float distance,
        float averageSpeed,
        float maxSpeed,
        float totalCost,
        float carbonSaved,
        int caloriesBurned,
        String weather,
        int temperature,
        String status,
        List<String> incidentReports,
        List<String> photos
){
}
