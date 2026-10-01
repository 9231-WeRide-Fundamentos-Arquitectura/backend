package org.example.backendweride.platform.garage.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
@Schema(description = "A vehicle of the fleet with its technical, pricing and maintenance data.")
public record VehicleResource(
        String id,
        String brand, String model, Integer year, Integer battery, Integer maxSpeed,
        Integer range, Double weight, String color, String licensePlate, String location,
        String status, String type, String companyId, Double pricePerMinute, String image,
        List<String> features, String maintenanceStatus, Date lastMaintenance,
        Date nextMaintenance, Double totalKilometers, Double rating
) {}