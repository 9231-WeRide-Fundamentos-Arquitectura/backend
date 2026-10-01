package org.example.backendweride.platform.trip.infrastructure.persistence.jpa;

import org.example.backendweride.platform.trip.domain.aggregates.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {

}
