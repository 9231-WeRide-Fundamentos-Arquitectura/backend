package org.example.backendweride.platform.location.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import org.example.backendweride.platform.location.domain.valueobjects.Coordinates;
import org.example.backendweride.platform.location.domain.valueobjects.OperatingHours;

import java.util.List;

@Schema(description = "A pickup or drop-off location with its coordinates, capacity and operating hours.")
public record LocationResource(
        Long id,
        String name,
        String address,
        Coordinates coordinates,
        String type,
        int capacity,
        int availableSpots,
        boolean isActive,
        OperatingHours operatingHours,
        List<String> amenities,
        String district,
        String description,
        String image
) {
}
