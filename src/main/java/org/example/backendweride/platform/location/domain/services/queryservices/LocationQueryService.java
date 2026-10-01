package org.example.backendweride.platform.location.domain.services.queryservices;

import org.example.backendweride.platform.location.domain.model.aggregates.Location;

import java.util.List;
import java.util.Optional;

public interface LocationQueryService {
    Optional<List<Location>> handle();
}
