package org.example.backendweride.platform.garage.interfaces.rest.transform;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.interfaces.rest.resources.VehicleResource;

public class VehicleResourceFromEntityAssembler {
    public static VehicleResource toResourceFromEntity(Vehicle entity) {
        return new VehicleResource(
                entity.getId().toString(),
                entity.getBrand(), entity.getModel(), entity.getYear(), entity.getBattery(),
                entity.getMaxSpeed(), entity.getRange(), entity.getWeight(), entity.getColor(),
                entity.getLicensePlate(), entity.getLocation(), entity.getStatus(), entity.getType(),
                entity.getCompanyId(), entity.getPricePerMinute(), entity.getImage(), entity.getFeatures(),
                entity.getMaintenanceStatus(), entity.getLastMaintenance(), entity.getNextMaintenance(),
                entity.getTotalKilometers(), entity.getRating()
        );
    }
}