package org.example.backendweride.platform.travelhistory.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "A record of a past ride in the travel history of a user.")
public record TravelHistoryResource(
        Long id,
        Long userId,
        String location,
        String vehicle,
        String image,
        String tripDuration,
        String travelDistance,
        Date createdAt
){
}
