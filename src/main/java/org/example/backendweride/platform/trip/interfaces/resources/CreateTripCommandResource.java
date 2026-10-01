package org.example.backendweride.platform.trip.interfaces.resources;

import org.example.backendweride.platform.trip.domain.valueobjects.RouteCoordinates;

import java.util.Date;
import java.util.List;

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
