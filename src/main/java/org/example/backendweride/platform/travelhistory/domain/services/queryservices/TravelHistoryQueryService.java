package org.example.backendweride.platform.travelhistory.domain.services.queryservices;

import org.example.backendweride.platform.travelhistory.domain.model.aggregates.TravelHistory;
import org.example.backendweride.platform.travelhistory.domain.model.queries.GetAllTravelsHistory;
import org.example.backendweride.platform.travelhistory.domain.model.queries.GetTravelsHistoryById;

import java.util.List;
import java.util.Optional;

public interface TravelHistoryQueryService {
    Optional<List<TravelHistory>> handle(GetTravelsHistoryById query);
    Optional<List<TravelHistory>> handle(GetAllTravelsHistory query);
}
