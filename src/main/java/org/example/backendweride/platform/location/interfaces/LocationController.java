package org.example.backendweride.platform.location.interfaces;

import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.example.backendweride.platform.location.domain.services.commandservices.LocationCommandService;
import org.example.backendweride.platform.location.domain.services.queryservices.LocationQueryService;
import org.example.backendweride.platform.location.interfaces.resources.CreateLocationResource;
import org.example.backendweride.platform.location.interfaces.resources.LocationResource;
import org.example.backendweride.platform.location.interfaces.transform.CreateLocationCommandFromResourceAssembler;
import org.example.backendweride.platform.location.interfaces.transform.LocationResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/location", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Locations", description = "Manage pickup and drop-off locations (stations) where vehicles are available.")
public class LocationController {
    private final LocationCommandService locationCommandService;
    private final LocationQueryService locationQueryService;

    public LocationController(
            LocationCommandService locationCommandService,
            LocationQueryService locationQueryService
    ) {
        this.locationQueryService = locationQueryService;
        this.locationCommandService = locationCommandService;
    }

    @Operation(summary = "Create a location", description = "Register a new location with its address, coordinates, capacity and operating hours.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location created successfully"),
            @ApiResponse(responseCode = "404", description = "Location could not be created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PostMapping
    public ResponseEntity<LocationResource> createLocation(@RequestBody CreateLocationResource locationResource) {
        var result = this.locationCommandService.handle(CreateLocationCommandFromResourceAssembler.toCommandFromResource(locationResource));

        return result.map(location -> new ResponseEntity<>(
                LocationResourceFromEntityAssembler.toResourceFromEntity(location), CREATED
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Get all locations", description = "Retrieve every registered location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No locations found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocation() {
        var result = this.locationQueryService.handle();
        return result.map(response ->
                new ResponseEntity<>(response, HttpStatus.OK
                )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }
}
