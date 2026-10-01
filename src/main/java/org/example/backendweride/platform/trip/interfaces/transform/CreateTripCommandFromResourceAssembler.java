package org.example.backendweride.platform.trip.interfaces.transform;

import org.example.backendweride.platform.trip.domain.commands.CreateTripCommand;
import org.example.backendweride.platform.trip.interfaces.resources.CreateTripCommandResource;

public class CreateTripCommandFromResourceAssembler {

    public static CreateTripCommand toCommandFromResource(CreateTripCommandResource resource) {
        return new CreateTripCommand(
                resource.bookingId(),
                resource.userId(),
                resource.vehicleId(),
                resource.startLocationId(),
                resource.endLocationId(),
                resource.route(),
                resource.routeCoordinates(),
                resource.startDate(),
                resource.endDate(),
                resource.duration(),
                resource.distance(),
                resource.averageSpeed(),
                resource.maxSpeed(),
                resource.totalCost(),
                resource.carbonSaved(),
                resource.caloriesBurned(),
                resource.weather(),
                resource.temperature(),
                resource.status(),
                resource.incidentReports(),
                resource.photos()
        );
    }
}
