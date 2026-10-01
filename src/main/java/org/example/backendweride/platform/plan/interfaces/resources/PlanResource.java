package org.example.backendweride.platform.plan.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A subscription plan with its pricing, usage limits and benefits.")
public record PlanResource(
        Long id,
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
