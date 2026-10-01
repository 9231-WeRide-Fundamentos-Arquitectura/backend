package org.example.backendweride.platform.plan.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Data required to create a subscription plan.")
public record CreatePlanResource(
        String name,
        String description,
        float price,
        String currency,
        float pricePerMinute,
        String duration,
        int  durationDays,
        int maxTripsPerDay,
        int maxMinutesPerTrip,
        int freeMinutesPerMonth,
        int discountPercentage,
        List<String> benefits,
        String color,
        boolean isPopular,
        boolean isActive
) {
}
