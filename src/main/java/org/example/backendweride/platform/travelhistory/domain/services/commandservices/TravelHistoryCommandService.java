package org.example.backendweride.platform.travelhistory.domain.services.commandservices;

import org.example.backendweride.platform.travelhistory.domain.model.aggregates.TravelHistory;
import org.example.backendweride.platform.travelhistory.domain.model.commands.CreateTravelHistoryCommand;
import org.example.backendweride.platform.travelhistory.domain.model.commands.UpdateTravelHistoryCommand;

import java.util.Optional;

public interface TravelHistoryCommandService {
    Optional<TravelHistory> handle(CreateTravelHistoryCommand travelHistoryCommand);
    Optional<TravelHistory> handle(UpdateTravelHistoryCommand command);

}
