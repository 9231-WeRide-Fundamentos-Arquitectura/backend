package org.example.backendweride.platform.garage.infrastructure.persistence.jpa;

import org.example.backendweride.platform.garage.domain.model.aggregates.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUserIdOrderByAddedAtDesc(Long userId);
    Optional<Favorite> findByUserIdAndVehicleId(Long userId, Long vehicleId);
}
