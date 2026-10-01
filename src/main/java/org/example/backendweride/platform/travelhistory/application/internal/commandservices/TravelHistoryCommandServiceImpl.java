package org.example.backendweride.platform.travelhistory.application.internal.commandservices;

import org.example.backendweride.platform.travelhistory.domain.model.aggregates.TravelHistory;
import org.example.backendweride.platform.travelhistory.domain.model.commands.CreateTravelHistoryCommand;
import org.example.backendweride.platform.travelhistory.domain.model.commands.UpdateTravelHistoryCommand;
import org.example.backendweride.platform.travelhistory.domain.services.commandservices.TravelHistoryCommandService;
import org.example.backendweride.platform.travelhistory.infrastructure.persistence.jpa.TravelHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * TravelHistoryCommandServiceImpl implements the TravelHistoryCommandService interface
 * to handle commands related to travel history.
 *
 * @summary This service provides methods to create and update travel history records.
 */
@Service
public class TravelHistoryCommandServiceImpl implements TravelHistoryCommandService {

    private final TravelHistoryRepository travelHistoryRepository;

    public TravelHistoryCommandServiceImpl(TravelHistoryRepository travelHistoryRepository) {
        this.travelHistoryRepository = travelHistoryRepository;
    }

    @Override
    public Optional<TravelHistory> handle(CreateTravelHistoryCommand travelHistoryCommand) {
        var travelHistory = new TravelHistory(travelHistoryCommand);
        var createTravelHistory = travelHistoryRepository.save(travelHistory);
        return Optional.of(createTravelHistory);
    }

    @Override
    public Optional<TravelHistory> handle(UpdateTravelHistoryCommand travelHistoryCommand) {
        var existingTravelHistory = travelHistoryRepository.findById(travelHistoryCommand.userId());
        if (existingTravelHistory.isPresent()) {
            var travelHistoryToUpdate = existingTravelHistory.get();
            travelHistoryToUpdate.updateFromCommand(travelHistoryCommand);
            var updatedTravelHistory = travelHistoryRepository.save(travelHistoryToUpdate);
            return Optional.of(updatedTravelHistory);
        }
        return Optional.empty();
    }


}
