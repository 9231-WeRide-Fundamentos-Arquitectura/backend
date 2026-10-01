package org.example.backendweride.platform.garage.interfaces.rest;

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

@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehiclesController {

    private final VehicleCommandService vehicleCommandService;
    private final VehicleQueryService vehicleQueryService;

    public VehiclesController(VehicleCommandService vehicleCommandService, VehicleQueryService vehicleQueryService) {
        this.vehicleCommandService = vehicleCommandService;
        this.vehicleQueryService = vehicleQueryService;
    }

    // 1. POST: Crear
    @PostMapping
    public ResponseEntity<VehicleResource> createVehicle(@RequestBody CreateVehicleResource resource) {
        var command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        var vehicleId = vehicleCommandService.handle(command);

        var getVehicleByIdQuery = new GetVehicleByIdQuery(vehicleId);
        var vehicle = vehicleQueryService.handle(getVehicleByIdQuery);

        return vehicle.map(value -> new ResponseEntity<>(VehicleResourceFromEntityAssembler.toResourceFromEntity(value), HttpStatus.CREATED)).orElseGet(() -> ResponseEntity.badRequest().build());

    }

    // 2. GET: Listar Todos
    @GetMapping
    public ResponseEntity<List<VehicleResource>> getAllVehicles() {
        var getAllVehiclesQuery = new GetAllVehiclesQuery();
        var vehicles = vehicleQueryService.handle(getAllVehiclesQuery);

        var resources = vehicles.stream()
                .map(VehicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    // 3. GET: Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResource> getVehicleById(@PathVariable Long id) {
        var getVehicleByIdQuery = new GetVehicleByIdQuery(id);
        var vehicle = vehicleQueryService.handle(getVehicleByIdQuery);

        return vehicle.map(value -> ResponseEntity.ok(VehicleResourceFromEntityAssembler.toResourceFromEntity(value))).orElseGet(() -> ResponseEntity.notFound().build());

    }

    // 4. PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResource> updateVehicle(@PathVariable Long id, @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedVehicle = vehicleCommandService.handle(command);

        return updatedVehicle.map(vehicle -> ResponseEntity.ok(VehicleResourceFromEntityAssembler.toResourceFromEntity(vehicle))).orElseGet(() -> ResponseEntity.notFound().build());

    }

    // 5. DELETE: Eliminar (NUEVO)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
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