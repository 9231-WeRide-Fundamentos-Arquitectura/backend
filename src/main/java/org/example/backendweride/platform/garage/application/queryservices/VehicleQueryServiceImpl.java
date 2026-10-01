package org.example.backendweride.platform.garage.application.queryservices;

import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.domain.model.queries.GetAllVehiclesQuery;
import org.example.backendweride.platform.garage.domain.model.queries.GetVehicleByIdQuery;
import org.example.backendweride.platform.garage.domain.services.VehicleQueryService;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleQueryServiceImpl implements VehicleQueryService {
    private final VehicleRepository vehicleRepository;

    public VehicleQueryServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Optional<Vehicle> handle(GetVehicleByIdQuery query) {
        return vehicleRepository.findById(query.id());
    }

    @Override
    public List<Vehicle> handle(GetAllVehiclesQuery query) {
        return vehicleRepository.findAll();
    }
}