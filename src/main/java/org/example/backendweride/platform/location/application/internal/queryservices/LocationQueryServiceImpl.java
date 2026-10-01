package org.example.backendweride.platform.location.application.internal.queryservices;

import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.example.backendweride.platform.location.domain.services.queryservices.LocationQueryService;
import org.example.backendweride.platform.location.infrastructure.persistence.jpa.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationQueryServiceImpl implements LocationQueryService {

    private final LocationRepository locationRepository;

    public LocationQueryServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Optional<List<Location>> handle() {
        List<Location> locations = this.locationRepository.findAll();
        return Optional.of(locations);
    }
}
