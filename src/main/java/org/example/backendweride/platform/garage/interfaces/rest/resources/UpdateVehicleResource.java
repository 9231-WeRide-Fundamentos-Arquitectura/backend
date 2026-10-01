package org.example.backendweride.platform.garage.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Data used to update an existing vehicle.")
public record UpdateVehicleResource(
        String brand, String model, Integer year, Integer battery,
        Integer maxSpeed, Integer range, Double weight, String color,
        String licensePlate, String location, String status, String type,
        String companyId, Double pricePerMinute, String image,
        List<String> features, String maintenanceStatus,
        Date lastMaintenance, Date nextMaintenance,
        Double totalKilometers, Double rating
) {}