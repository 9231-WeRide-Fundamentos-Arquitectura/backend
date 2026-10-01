package org.example.backendweride.platform.location.application.internal.commandservices;

import org.example.backendweride.platform.location.domain.commands.CreateLocationCommand;
import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.example.backendweride.platform.location.domain.services.commandservices.LocationCommandService;
import org.example.backendweride.platform.location.infrastructure.persistence.jpa.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationCommandServiceImpl implements LocationCommandService {
    private final LocationRepository locationRepository;

    public LocationCommandServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Optional<Location> handle(CreateLocationCommand locationCommand) {
        var location = new Location(locationCommand);
        var createLocation = this.locationRepository.save(location);
        return Optional.of(createLocation);

    }
}
