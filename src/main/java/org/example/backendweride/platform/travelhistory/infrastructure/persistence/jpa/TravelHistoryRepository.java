package org.example.backendweride.platform.travelhistory.infrastructure.persistence.jpa;

import org.example.backendweride.platform.travelhistory.domain.model.aggregates.TravelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelHistoryRepository extends JpaRepository<TravelHistory, Long> {
    List<TravelHistory> findByUserId(Long userId);
}
