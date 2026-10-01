package org.example.backendweride.platform.garage.domain.services;

import org.example.backendweride.platform.garage.domain.model.commands.CreateVehicleCommand;
import org.example.backendweride.platform.garage.domain.model.commands.DeleteVehicleCommand;
import org.example.backendweride.platform.garage.domain.model.commands.UpdateVehicleCommand;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import java.util.Optional;

public interface VehicleCommandService {
    Long handle(CreateVehicleCommand command);
    Optional<Vehicle> handle(UpdateVehicleCommand command);
    void handle(DeleteVehicleCommand command);
}