package org.example.backendweride.platform.garage.infrastructure.persistence.jpa;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}