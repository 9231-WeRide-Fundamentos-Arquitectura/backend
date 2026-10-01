package org.example.backendweride.platform.location.infrastructure.persistence.jpa;

import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}
