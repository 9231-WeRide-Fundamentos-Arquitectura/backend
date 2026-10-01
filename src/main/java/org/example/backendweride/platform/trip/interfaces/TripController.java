package org.example.backendweride.platform.trip.interfaces;

import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;

import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.trip.application.internal.commands.TripCommandServiceImpl;
import org.example.backendweride.platform.trip.application.internal.queries.TripQueryServiceImpl;
import org.example.backendweride.platform.trip.domain.aggregates.Trip;
import org.example.backendweride.platform.trip.domain.queries.DeleteTripById;
import org.example.backendweride.platform.trip.domain.services.commands.TripCommandService;
import org.example.backendweride.platform.trip.domain.services.queries.TripQueryService;
import org.example.backendweride.platform.trip.interfaces.resources.CreateTripCommandResource;
import org.example.backendweride.platform.trip.interfaces.resources.TripResource;
import org.example.backendweride.platform.trip.interfaces.transform.CreateTripCommandFromResourceAssembler;
import org.example.backendweride.platform.trip.interfaces.transform.TripResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/trips", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Trips", description = "Record, list and delete the trips of the authenticated user.")
public class TripController {
    private final TripCommandService tripCommandService;
    private final TripQueryService tripQueryService;
    public TripController(
            TripCommandServiceImpl tripCommandService,
            TripQueryService tripQueryService
    ) {
        this.tripCommandService = tripCommandService;
        this.tripQueryService = tripQueryService;
    }

    @Operation(summary = "Create a trip", description = "Record a completed trip (route, cost, duration, distance and environmental data) for the authenticated user. The userId in the body must match the authenticated account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trip created successfully"),
            @ApiResponse(responseCode = "404", description = "Trip could not be created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @PostMapping
    public ResponseEntity<TripResource> createTrip(@RequestBody CreateTripCommandResource createTripCommandResource, Authentication authentication) {
        CurrentUser.requireSelf(authentication, createTripCommandResource.userId());
        var result = this.tripCommandService.handle(CreateTripCommandFromResourceAssembler.toCommandFromResource(createTripCommandResource));
        return result.map(response -> new ResponseEntity<>(
                TripResourceFromEntityAssembler.toResource(response), HttpStatus.CREATED
                )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Get my trips", description = "Retrieve every trip that belongs to the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trips retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No trips found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips(Authentication authentication) {
        var userId = String.valueOf(CurrentUser.id(authentication));
        var result = this.tripQueryService.handle();
        return result.map(response ->
                new ResponseEntity<>(response.stream().filter(t -> userId.equals(t.getUserId())).toList(), HttpStatus.OK
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete a trip", description = "Delete a trip of the authenticated user using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trip deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTripById(@Parameter(description = "Unique identifier of the trip") @PathVariable Long id, Authentication authentication) {
        var trip = this.tripQueryService.handle().orElse(List.of()).stream()
                .filter(t -> id.equals(t.getId())).findFirst();
        if (trip.isEmpty()) return ResponseEntity.notFound().build();
        CurrentUser.requireSelf(authentication, trip.get().getUserId());
        this.tripCommandService.handle(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
