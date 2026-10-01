package org.example.backendweride.platform.plan.interfaces;

import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;

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
@Tag(name = "Plans", description = "Manage the subscription plans offered to riders, including pricing, limits and benefits.")
public class PlanController {

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    public PlanController(PlanCommandService planCommandService, PlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    @Operation(summary = "Create a plan", description = "Create a new subscription plan with its pricing, usage limits and benefits.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plan created successfully"),
            @ApiResponse(responseCode = "404", description = "Plan could not be created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PostMapping
    public ResponseEntity<PlanResource> createPlan(@RequestBody CreatePlanResource planResource) {
        var result = this.planCommandService.handle(CreatePlanCommandFronResourceAssembler.toCommandFromResource(planResource));
        return result.map(response -> new ResponseEntity<>(
                PlanResourceFromEntity.toPlanResource(response), HttpStatus.CREATED
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @Operation(summary = "Get plan by ID", description = "Retrieve a subscription plan using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan found"),
            @ApiResponse(responseCode = "404", description = "Plan not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlanResource> findPlanById(@Parameter(description = "Unique identifier of the plan") @PathVariable Long id) {
        var result = this.planQueryService.handle(new GetPlanById(id));
        return result.map(response -> new ResponseEntity<>(
                PlanResourceFromEntity.toPlanResource(response), HttpStatus.OK
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Get all plans", description = "Retrieve every available subscription plan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plans retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No plans found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public ResponseEntity<List<Plan>> findAllPlans() {
        var result = this.planQueryService.handle();
        return result.map(response -> new ResponseEntity<>(
                response, HttpStatus.OK
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete a plan", description = "Delete a subscription plan using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plan deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanById(@Parameter(description = "Unique identifier of the plan") @PathVariable Long id) {
        this.planCommandService.handle(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
