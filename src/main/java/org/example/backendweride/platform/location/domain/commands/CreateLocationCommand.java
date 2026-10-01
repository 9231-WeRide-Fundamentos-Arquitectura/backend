package org.example.backendweride.platform.location.domain.commands;

import org.example.backendweride.platform.location.domain.valueobjects.Coordinates;
import org.example.backendweride.platform.location.domain.valueobjects.OperatingHours;

import java.util.List;

public record CreateLocationCommand(
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
