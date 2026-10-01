package org.example.backendweride.platform.travelhistory.interfaces.resources;

public record UpdateTravelHistoryResource(
        Long userId,
        String location,
        String vehicle,
        String image,
        String tripDuration,
        String travelDistance
){}
