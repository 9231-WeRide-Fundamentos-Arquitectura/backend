package org.example.backendweride.platform.trip.application.internal.commands;

import org.example.backendweride.platform.trip.domain.aggregates.Trip;
import org.example.backendweride.platform.trip.domain.commands.CreateTripCommand;
import org.example.backendweride.platform.trip.domain.services.commands.TripCommandService;
import org.example.backendweride.platform.trip.infrastructure.persistence.jpa.TripRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TripCommandServiceImpl implements TripCommandService {
    private final TripRepository tripRepository;

    public TripCommandServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Optional<Trip> handle(CreateTripCommand command) {
        var trip = new Trip(command);
        var result = this.tripRepository.save(trip);
        return Optional.of(result);
    }

    @Override
    public void handle(Long id) {
        this.tripRepository.deleteById(id);
    }
}
