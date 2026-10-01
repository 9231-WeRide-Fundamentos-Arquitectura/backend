package org.example.backendweride.platform.location.interfaces.transform;

import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.example.backendweride.platform.location.interfaces.resources.LocationResource;

public class LocationResourceFromEntityAssembler {
    public static LocationResource toResourceFromEntity(Location entity) {
        return new LocationResource(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCoordinates(),
                entity.getType(),
                entity.getCapacity(),
                entity.getAvailableSpots(),
                entity.isActive(),
                entity.getOperatingHours(),
                entity.getAmenities(),
                entity.getDistrict(),
                entity.getDescription(),
                entity.getImage()
        );
    }
}
