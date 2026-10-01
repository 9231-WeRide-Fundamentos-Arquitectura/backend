package org.example.backendweride.platform.garage.application.commandservices;

import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.domain.model.commands.CreateVehicleCommand;
import org.example.backendweride.platform.garage.domain.model.commands.DeleteVehicleCommand; // Importar
import org.example.backendweride.platform.garage.domain.model.commands.UpdateVehicleCommand;
import org.example.backendweride.platform.garage.domain.services.VehicleCommandService;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Long handle(CreateVehicleCommand command) {
        var vehicle = new Vehicle(command);
        vehicleRepository.save(vehicle);
        return vehicle.getId();
    }

    @Override
    public Optional<Vehicle> handle(UpdateVehicleCommand command) {
        return vehicleRepository.findById(command.id())
                .map(vehicleToUpdate -> vehicleRepository.save(vehicleToUpdate.updateInformation(command)));
    }


    @Override
    public void handle(DeleteVehicleCommand command) {
        if (!vehicleRepository.existsById(command.id())) {
            throw new IllegalArgumentException("Vehicle with id " + command.id() + " does not exist");
        }
        vehicleRepository.deleteById(command.id());
    }
}