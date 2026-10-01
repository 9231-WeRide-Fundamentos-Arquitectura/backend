package org.example.backendweride.platform.plan.interfaces.transform;

import org.example.backendweride.platform.plan.domain.commands.CreatePlanCommand;
import org.example.backendweride.platform.plan.interfaces.resources.CreatePlanResource;

public class CreatePlanCommandFronResourceAssembler {

    public static CreatePlanCommand toCommandFromResource(CreatePlanResource resource) {
        return new CreatePlanCommand(
                resource.name(),
                resource.description(),
                resource.price(),
                resource.currency(),
                resource.pricePerMinute(),
                resource.duration(),
                resource.durationDays(),
                resource.maxTripsPerDay(),
                resource.maxMinutesPerTrip(),
                resource.freeMinutesPerMonth(),
                resource.discountPercentage(),
                resource.benefits(),
                resource.color(),
                resource.isPopular(),
                resource.isActive()
        );
    }
}
