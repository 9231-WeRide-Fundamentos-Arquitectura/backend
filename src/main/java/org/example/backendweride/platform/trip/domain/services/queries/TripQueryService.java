package org.example.backendweride.platform.trip.domain.services.queries;

import org.example.backendweride.platform.trip.domain.aggregates.Trip;
import org.example.backendweride.platform.trip.domain.queries.DeleteTripById;

import java.util.List;
import java.util.Optional;

public interface TripQueryService {
    Optional<List<Trip>> handle();
}
