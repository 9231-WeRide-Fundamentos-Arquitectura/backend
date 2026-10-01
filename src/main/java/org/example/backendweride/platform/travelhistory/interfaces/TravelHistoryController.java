package org.example.backendweride.platform.travelhistory.interfaces;

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
@Tag(name = "Travel History", description = "Create, Obtain and Update Travel History")
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
    @Operation(summary = "Create Travel History", description = "Create a new travel history record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Travel history created successfully"),
            @ApiResponse(responseCode = "404", description = "Related entity not found")
    })
    public ResponseEntity<TravelHistoryResource> createTravelHistory(@RequestBody CreateTravelHistoryResource travelHistoryResource) {
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
    @Operation(summary = "Get All Travel Histories", description = "Retrieve all travel history records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Travel histories found"),
            @ApiResponse(responseCode = "404", description = "No travel histories found")
    })
    public ResponseEntity<List<TravelHistory>> getAllTravelHistories() {
        var result = travelHistoryQueryService.handle(new org.example.backendweride.platform.travelhistory.domain.model.queries.GetAllTravelsHistory());
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
    @Operation(summary = "Get Travel History by User ID", description = "Retrieve travel history records for a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Travel history found"),
            @ApiResponse(responseCode = "404", description = "Travel history not found")
    })
    public ResponseEntity<List<TravelHistory>> getTravelHistoryById(@PathVariable Long userId) {
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
    @Operation(summary = "Update Travel History", description = "Update an existing travel history record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Travel history updated successfully"),
            @ApiResponse(responseCode = "404", description = "Travel history not found")
    })
    public ResponseEntity<TravelHistoryResource> updateTravelHistory(
            @PathVariable Long id,
            @RequestBody UpdateTravelHistoryResource resource) {
        var command = UpdateTravelHistoryCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = travelHistoryCommandService.handle(command);
        return result.map(travelHistory -> ResponseEntity.ok(
                TravelHistoryResourceFromEntityAssembler.toTravelHistoryFromEntity(travelHistory)
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
