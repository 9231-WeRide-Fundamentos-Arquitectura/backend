package org.example.backendweride.platform.travelhistory.interfaces;

import io.swagger.v3.oas.annotations.Parameter;

import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.travelhistory.domain.model.aggregates.TravelHistory;
import org.example.backendweride.platform.travelhistory.domain.model.queries.GetTravelsHistoryById;
import org.example.backendweride.platform.travelhistory.domain.services.commandservices.TravelHistoryCommandService;
import org.example.backendweride.platform.travelhistory.domain.services.queryservices.TravelHistoryQueryService;
import org.example.backendweride.platform.travelhistory.interfaces.resources.CreateTravelHistoryResource;
import org.example.backendweride.platform.travelhistory.interfaces.resources.TravelHistoryResource;
import org.example.backendweride.platform.travelhistory.interfaces.resources.UpdateTravelHistoryResource;
import org.example.backendweride.platform.travelhistory.interfaces.transform.CreateTravelHistoryCommandFromResourceAssembler;
import org.example.backendweride.platform.travelhistory.interfaces.transform.TravelHistoryResourceFromEntityAssembler;
import org.example.backendweride.platform.travelhistory.interfaces.transform.UpdateTravelHistoryCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * TravelHistoryController handles HTTP requests related to travel history.
 *
 * @summary This controller provides endpoints for creating and obtaining travel history records.
 */
@RestController
@RequestMapping(value = "/api/v1/travel-history", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Travel History", description = "Keep a personal log of past rides: create, list and update the travel history records of the authenticated user.")
public class TravelHistoryController {
    private final TravelHistoryCommandService travelHistoryCommandService;
    private final TravelHistoryQueryService travelHistoryQueryService;

    public TravelHistoryController(
            TravelHistoryCommandService travelHistoryCommandService,
            TravelHistoryQueryService travelHistoryQueryService
    ) {
        this.travelHistoryCommandService = travelHistoryCommandService;
       this.travelHistoryQueryService = travelHistoryQueryService;
    }

    /**
     * Create a new travel history record.
     *
     * @param travelHistoryResource The travel history details.
     * @return ResponseEntity containing the created travel history or an error status.
     */
    @PostMapping
    @Operation(summary = "Create Travel History", description = "Create a new travel history record (location, vehicle, duration and distance) for the authenticated user. The userId in the body must match the authenticated account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Travel history created successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId in the body belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Related entity not found")
    })
    public ResponseEntity<TravelHistoryResource> createTravelHistory(@RequestBody CreateTravelHistoryResource travelHistoryResource, Authentication authentication) {
        CurrentUser.requireSelf(authentication, travelHistoryResource.userId());
        var result = travelHistoryCommandService.handle(CreateTravelHistoryCommandFromResourceAssembler.toCommandFronResource(travelHistoryResource));
        return result.map(travelHistory -> new ResponseEntity<>(
                TravelHistoryResourceFromEntityAssembler.toTravelHistoryFromEntity(travelHistory), CREATED
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Get all travel history records.
     *
     * @return ResponseEntity containing the list of all travel history records or an error status.
     */
    @GetMapping
    @Operation(summary = "Get My Travel Histories", description = "Retrieve all travel history records of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Travel histories found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "No travel histories found")
    })
    public ResponseEntity<List<TravelHistory>> getAllTravelHistories(Authentication authentication) {
        var result = travelHistoryQueryService.handle(new GetTravelsHistoryById(CurrentUser.id(authentication)));
        return result.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Get travel history records by user ID.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing the list of travel history records or an error status.
     */
    @GetMapping("{userId}")
    @Operation(summary = "Get Travel History by User ID", description = "Retrieve the travel history records of a specific user. Only the user themselves can query their own records.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Travel history found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The history belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Travel history not found")
    })
    public ResponseEntity<List<TravelHistory>> getTravelHistoryById(@Parameter(description = "ID of the user whose travel history is requested") @PathVariable Long userId, Authentication authentication) {
        CurrentUser.requireSelf(authentication, userId);
        var result = travelHistoryQueryService.handle(new GetTravelsHistoryById(userId));
        return result.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Update an existing travel history record.
     *
     * @param id       The ID of the travel history record to update.
     * @param resource The updated travel history details.
     * @return ResponseEntity containing the updated travel history or an error status.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Travel History record", description = "Update the location, vehicle, image, duration and distance of an existing travel history record owned by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Travel history updated successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId in the body belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Travel history not found or not owned by the user")
    })
    public ResponseEntity<TravelHistoryResource> updateTravelHistory(
            @Parameter(description = "Unique identifier of the travel history record") @PathVariable Long id,
            @RequestBody UpdateTravelHistoryResource resource,
            Authentication authentication) {
        CurrentUser.requireSelf(authentication, resource.userId());
        var owned = travelHistoryQueryService.handle(new GetTravelsHistoryById(CurrentUser.id(authentication)))
                .orElse(List.of()).stream().anyMatch(t -> id.equals(t.getId()));
        if (!owned) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        var command = UpdateTravelHistoryCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = travelHistoryCommandService.handle(command);
        return result.map(travelHistory -> ResponseEntity.ok(
                TravelHistoryResourceFromEntityAssembler.toTravelHistoryFromEntity(travelHistory)
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
