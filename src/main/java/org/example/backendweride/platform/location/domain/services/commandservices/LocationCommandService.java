package org.example.backendweride.platform.location.domain.services.commandservices;

import org.example.backendweride.platform.location.domain.commands.CreateLocationCommand;
import org.example.backendweride.platform.location.domain.model.aggregates.Location;

import java.util.Optional;

public interface LocationCommandService {
    Optional<Location> handle(CreateLocationCommand locationCommand);
}
