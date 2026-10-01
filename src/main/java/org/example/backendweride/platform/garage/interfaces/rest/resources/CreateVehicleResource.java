package org.example.backendweride.platform.garage.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Data required to register a new vehicle in the fleet.")
public record CreateVehicleResource(
        String brand,
        String model,
        Integer year,
        Integer battery,
        Integer maxSpeed,
        Integer range,
        double weight,
        String color,
        String licensePlate,
        String location,
        String status,
        String type,
        String companyId,
        double pricePerMinute,
        String image,
        List<String> features,
        String maintenanceStatus,
        Date lastMaintenance,
        Date nextMaintenance,
        double totalKilometers,
        double rating
) {}