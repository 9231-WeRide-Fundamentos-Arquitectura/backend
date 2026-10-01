package org.example.backendweride.platform.garage.interfaces.rest.resources;

import java.util.Date;
import java.util.List;

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