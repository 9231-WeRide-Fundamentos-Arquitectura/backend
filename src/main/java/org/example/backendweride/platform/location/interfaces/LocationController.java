package org.example.backendweride.platform.location.interfaces;

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
@Tag(name = "Locations")
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

    @PostMapping
    public ResponseEntity<LocationResource> createLocation(@RequestBody CreateLocationResource locationResource) {
        var result = this.locationCommandService.handle(CreateLocationCommandFromResourceAssembler.toCommandFromResource(locationResource));

        return result.map(location -> new ResponseEntity<>(
                LocationResourceFromEntityAssembler.toResourceFromEntity(location), CREATED
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocation() {
        var result = this.locationQueryService.handle();
        return result.map(response ->
                new ResponseEntity<>(response, HttpStatus.OK
                )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }
}
