package org.example.backendweride.platform.plan.domain.services.queries;

import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.domain.queries.GetPlanById;

import java.util.List;
import java.util.Optional;

public interface PlanQueryService {
    Optional<Plan> handle(GetPlanById planId);
    Optional<List<Plan>> handle();
}
