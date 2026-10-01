package org.example.backendweride.platform.plan.infrastructure.persistence.jpa;

import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

}
