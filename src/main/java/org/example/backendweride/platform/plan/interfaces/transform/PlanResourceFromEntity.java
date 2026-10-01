package org.example.backendweride.platform.plan.interfaces.transform;

import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.interfaces.resources.PlanResource;

public class PlanResourceFromEntity {
    public static PlanResource toPlanResource(Plan plan) {
        return new PlanResource(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getPricePerMinute(),
                plan.getDuration(),
                plan.getDurationDays(),
                plan.getMaxTripsPerDay(),
                plan.getMaxMinutesPerTrip(),
                plan.getFreeMinutesPerMonth(),
                plan.getDiscountPercentage(),
                plan.getBenefits(),
                plan.getColor(),
                plan.isPopular(),
                plan.isActive()
        );
    }
}
