package org.example.backendweride.platform.garage.interfaces.rest.transform;
import org.example.backendweride.platform.garage.domain.model.commands.UpdateVehicleCommand;
import org.example.backendweride.platform.garage.interfaces.rest.resources.UpdateVehicleResource;

public class UpdateVehicleCommandFromResourceAssembler {
    public static UpdateVehicleCommand toCommandFromResource(Long id, UpdateVehicleResource resource) {
        return new UpdateVehicleCommand(
                id,
                resource.brand(), resource.model(), resource.year(), resource.battery(),
                resource.maxSpeed(), resource.range(), resource.weight(), resource.color(),
                resource.licensePlate(), resource.location(), resource.status(), resource.type(),
                resource.companyId(), resource.pricePerMinute(), resource.image(), resource.features(),
                resource.maintenanceStatus(), resource.lastMaintenance(), resource.nextMaintenance(),
                resource.totalKilometers(), resource.rating()
        );
    }
}