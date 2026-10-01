package org.example.backendweride.platform.trip.interfaces.transform;

import org.example.backendweride.platform.trip.domain.aggregates.Trip;
import org.example.backendweride.platform.trip.interfaces.resources.TripResource;

public class TripResourceFromEntityAssembler {

    public static TripResource toResource(Trip entity) {
        return new TripResource(
                entity.getId(),
                entity.getBookingId(),
                entity.getUserId(),
                entity.getVehicleId(),
                entity.getStartLocationId(),
                entity.getEndLocationId(),
                entity.getRoute(),
                entity.getRouteCoordinates(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getDuration(),
                entity.getDistance(),
                entity.getAverageSpeed(),
                entity.getMaxSpeed(),
                entity.getTotalCost(),
                entity.getMaxSpeed(),
                entity.getCaloriesBurned(),
                entity.getWeather(),
                entity.getTemperature(),
                entity.getStatus(),
                entity.getIncidentReports(),
                entity.getPhotos()
        );
    }
}
