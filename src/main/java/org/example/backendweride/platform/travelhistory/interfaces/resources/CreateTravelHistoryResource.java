package org.example.backendweride.platform.travelhistory.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data required to create a travel history record. userId must match the authenticated account.")
public record CreateTravelHistoryResource(
        Long userId,
        String location,
        String vehicle,
        String image,
        String tripDuration,
        String travelDistance
) {
}
