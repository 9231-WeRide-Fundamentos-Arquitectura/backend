package org.example.backendweride.platform.plan.application.internal.commands;

import org.example.backendweride.platform.plan.domain.commands.CreatePlanCommand;
import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.domain.services.commands.PlanCommandService;
import org.example.backendweride.platform.plan.infrastructure.persistence.jpa.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanCommandServiceImpl implements PlanCommandService {
    private final PlanRepository planRepository;

    public PlanCommandServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }
    @Override
    public Optional<Plan> handle(CreatePlanCommand command) {
        var plan = new Plan(command);
        var result = this.planRepository.save(plan);
        return Optional.of(result);
    }

    @Override
    public void handle(Long id) {
        this.planRepository.deleteById(id);
    }

}
