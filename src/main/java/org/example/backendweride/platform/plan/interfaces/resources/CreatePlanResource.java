package org.example.backendweride.platform.plan.interfaces.resources;

import java.util.List;

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
