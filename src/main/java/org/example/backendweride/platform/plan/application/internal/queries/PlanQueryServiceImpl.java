package org.example.backendweride.platform.plan.application.internal.queries;

import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.domain.queries.GetPlanById;
import org.example.backendweride.platform.plan.domain.services.queries.PlanQueryService;
import org.example.backendweride.platform.plan.infrastructure.persistence.jpa.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanQueryServiceImpl implements PlanQueryService {
    private final PlanRepository planRepository;
    public PlanQueryServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Optional<Plan> handle(GetPlanById planId) {
        return this.planRepository.findById(planId.id());
    }

    @Override
    public Optional<List<Plan>> handle() {
        var result = this.planRepository.findAll();
        return Optional.of(result);
    }
}
