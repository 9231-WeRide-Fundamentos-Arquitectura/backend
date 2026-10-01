package org.example.backendweride.platform.plan.domain.commands;

import java.util.List;

public record CreatePlanCommand(
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
