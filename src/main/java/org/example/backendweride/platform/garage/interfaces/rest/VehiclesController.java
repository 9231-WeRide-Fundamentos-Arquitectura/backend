package org.example.backendweride.platform.garage.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;

import org.example.backendweride.platform.garage.domain.model.commands.DeleteVehicleCommand; // IMPORTANTE: Nueva importación
import org.example.backendweride.platform.garage.domain.model.queries.GetAllVehiclesQuery;
import org.example.backendweride.platform.garage.domain.model.queries.GetVehicleByIdQuery;
import org.example.backendweride.platform.garage.domain.services.VehicleCommandService;
import org.example.backendweride.platform.garage.domain.services.VehicleQueryService;
import org.example.backendweride.platform.garage.interfaces.rest.resources.CreateVehicleResource;
import org.example.backendweride.platform.garage.interfaces.rest.resources.UpdateVehicleResource;
import org.example.backendweride.platform.garage.interfaces.rest.resources.VehicleResource;
import org.example.backendweride.platform.garage.interfaces.rest.transform.CreateVehicleCommandFromResourceAssembler;
import org.example.backendweride.platform.garage.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;
import org.example.backendweride.platform.garage.interfaces.rest.transform.VehicleResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Vehicles", description = "Manage the vehicle fleet: register, list, consult, update and remove vehicles.")
@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehiclesController {

    private final VehicleCommandService vehicleCommandService;
    private final VehicleQueryService vehicleQueryService;
    private final org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository bookings;

    public VehiclesController(VehicleCommandService vehicleCommandService, VehicleQueryService vehicleQueryService,
            org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository bookings) {
        this.vehicleCommandService = vehicleCommandService;
        this.vehicleQueryService = vehicleQueryService;
        this.bookings = bookings;
    }

    private static Double round1(Double value) {
        return value == null ? null : Math.round(value * 10) / 10.0;
    }

    private VehicleResource vehicleResource(org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle vehicle) {
        return VehicleResourceFromEntityAssembler.toResourceFromEntity(vehicle, round1(bookings.averageRating(vehicle.getId().toString())));
    }

    // 1. POST: Crear
    @Operation(summary = "Create a vehicle", description = "Register a new vehicle in the fleet and return the stored vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle data"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PostMapping
    public ResponseEntity<VehicleResource> createVehicle(@RequestBody CreateVehicleResource resource) {
        var command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        var vehicleId = vehicleCommandService.handle(command);

        var getVehicleByIdQuery = new GetVehicleByIdQuery(vehicleId);
        var vehicle = vehicleQueryService.handle(getVehicleByIdQuery);

        return vehicle.map(value -> new ResponseEntity<>(vehicleResource(value), HttpStatus.CREATED)).orElseGet(() -> ResponseEntity.badRequest().build());

    }

    // 2. GET: Listar Todos
    @Operation(summary = "Get all vehicles", description = "Retrieve every vehicle registered in the fleet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public ResponseEntity<List<VehicleResource>> getAllVehicles() {
        var getAllVehiclesQuery = new GetAllVehiclesQuery();
        var vehicles = vehicleQueryService.handle(getAllVehiclesQuery);

        // One grouped query instead of one per vehicle.
        var averages = new java.util.HashMap<String, Double>();
        bookings.averageRatingsByVehicle().forEach(row -> averages.put((String) row[0], round1(((Number) row[1]).doubleValue())));
        var resources = vehicles.stream()
                .map(v -> VehicleResourceFromEntityAssembler.toResourceFromEntity(v, averages.get(v.getId().toString())))
                .toList();

        return ResponseEntity.ok(resources);
    }

    // 3. GET: Buscar por ID
    @Operation(summary = "Get vehicle by ID", description = "Retrieve the details of a single vehicle using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResource> getVehicleById(@Parameter(description = "Unique identifier of the vehicle") @PathVariable Long id) {
        var getVehicleByIdQuery = new GetVehicleByIdQuery(id);
        var vehicle = vehicleQueryService.handle(getVehicleByIdQuery);

        return vehicle.map(value -> ResponseEntity.ok(vehicleResource(value))).orElseGet(() -> ResponseEntity.notFound().build());

    }

    // 4. PUT: Actualizar
    @Operation(summary = "Update a vehicle", description = "Update the data of an existing vehicle identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResource> updateVehicle(@Parameter(description = "Unique identifier of the vehicle") @PathVariable Long id, @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedVehicle = vehicleCommandService.handle(command);

        return updatedVehicle.map(vehicle -> ResponseEntity.ok(vehicleResource(vehicle))).orElseGet(() -> ResponseEntity.notFound().build());

    }

    // 5. DELETE: Eliminar (NUEVO)
    @Operation(summary = "Delete a vehicle", description = "Permanently remove a vehicle from the fleet using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@Parameter(description = "Unique identifier of the vehicle") @PathVariable Long id) {
        var deleteVehicleCommand = new DeleteVehicleCommand(id);
        try {
            vehicleCommandService.handle(deleteVehicleCommand);
            return ResponseEntity.noContent().build(); // 204 No Content (Éxito)
        } catch (IllegalArgumentException e) {
            // Capturamos la excepción lanzada por el servicio si el ID no existe
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
