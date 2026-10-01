package org.example.backendweride.platform.garage.domain.services;

import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.domain.model.queries.GetAllVehiclesQuery;
import org.example.backendweride.platform.garage.domain.model.queries.GetVehicleByIdQuery;

import java.util.List;
import java.util.Optional;

public interface VehicleQueryService {
    Optional<Vehicle> handle(GetVehicleByIdQuery query);

    List<Vehicle> handle(GetAllVehiclesQuery query);
}