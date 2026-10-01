package org.example.backendweride.platform.plan.interfaces;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.domain.queries.GetPlanById;
import org.example.backendweride.platform.plan.domain.services.commands.PlanCommandService;
import org.example.backendweride.platform.plan.domain.services.queries.PlanQueryService;
import org.example.backendweride.platform.plan.interfaces.resources.CreatePlanResource;
import org.example.backendweride.platform.plan.interfaces.resources.PlanResource;
import org.example.backendweride.platform.plan.interfaces.transform.CreatePlanCommandFronResourceAssembler;
import org.example.backendweride.platform.plan.interfaces.transform.PlanResourceFromEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/plans", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Plans")
public class PlanController {

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    public PlanController(PlanCommandService planCommandService, PlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    @PostMapping
    public ResponseEntity<PlanResource> createPlan(@RequestBody CreatePlanResource planResource) {
        var result = this.planCommandService.handle(CreatePlanCommandFronResourceAssembler.toCommandFromResource(planResource));
        return result.map(response -> new ResponseEntity<>(
                PlanResourceFromEntity.toPlanResource(response), HttpStatus.CREATED
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResource> findPlanById(@PathVariable Long id) {
        var result = this.planQueryService.handle(new GetPlanById(id));
        return result.map(response -> new ResponseEntity<>(
                PlanResourceFromEntity.toPlanResource(response), HttpStatus.OK
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<List<Plan>> findAllPlans() {
        var result = this.planQueryService.handle();
        return result.map(response -> new ResponseEntity<>(
                response, HttpStatus.OK
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanById(@PathVariable Long id) {
        this.planCommandService.handle(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
