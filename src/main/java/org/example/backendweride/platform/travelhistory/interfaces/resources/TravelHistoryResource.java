package org.example.backendweride.platform.travelhistory.interfaces.resources;

import java.util.Date;

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
