package org.example.backendweride.platform.travelhistory.application.internal.queryservices;

import org.example.backendweride.platform.travelhistory.domain.model.aggregates.TravelHistory;
import org.example.backendweride.platform.travelhistory.domain.model.queries.GetAllTravelsHistory;
import org.example.backendweride.platform.travelhistory.domain.model.queries.GetTravelsHistoryById;
import org.example.backendweride.platform.travelhistory.domain.services.queryservices.TravelHistoryQueryService;
import org.example.backendweride.platform.travelhistory.infrastructure.persistence.jpa.TravelHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * TravelHistoryQueryServiceImpl implements the TravelHistoryQueryService interface
 * to handle queries related to travel history.
 *
 * @summary This service provides methods to retrieve travel history records by user ID
 *          and to retrieve all travel history records.
 */
@Service
public class TravelHistoryQueryServiceImpl implements TravelHistoryQueryService {

    private final TravelHistoryRepository travelHistoryRepository;
    public TravelHistoryQueryServiceImpl(TravelHistoryRepository travelHistoryRepository) {
        this.travelHistoryRepository = travelHistoryRepository;
    }

    @Override
    public Optional<List<TravelHistory>> handle(GetTravelsHistoryById query) {
        var listTravelHistory = travelHistoryRepository.findByUserId(query.id());
        return Optional.of(listTravelHistory);
    }

    @Override
    public Optional<List<TravelHistory>> handle(GetAllTravelsHistory query) {
        List<TravelHistory> travelHistories = travelHistoryRepository.findAll();
        return Optional.of(travelHistories);
    }
}
