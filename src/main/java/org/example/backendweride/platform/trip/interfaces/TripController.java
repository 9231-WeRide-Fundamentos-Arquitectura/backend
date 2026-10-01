package org.example.backendweride.platform.trip.interfaces;

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
@Tag(name = "Trips")
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

    @PostMapping
    public ResponseEntity<TripResource> createTrip(@RequestBody CreateTripCommandResource createTripCommandResource, Authentication authentication) {
        CurrentUser.requireSelf(authentication, createTripCommandResource.userId());
        var result = this.tripCommandService.handle(CreateTripCommandFromResourceAssembler.toCommandFromResource(createTripCommandResource));
        return result.map(response -> new ResponseEntity<>(
                TripResourceFromEntityAssembler.toResource(response), HttpStatus.CREATED
                )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips(Authentication authentication) {
        var userId = String.valueOf(CurrentUser.id(authentication));
        var result = this.tripQueryService.handle();
        return result.map(response ->
                new ResponseEntity<>(response.stream().filter(t -> userId.equals(t.getUserId())).toList(), HttpStatus.OK
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTripById(@PathVariable Long id, Authentication authentication) {
        var trip = this.tripQueryService.handle().orElse(List.of()).stream()
                .filter(t -> id.equals(t.getId())).findFirst();
        if (trip.isEmpty()) return ResponseEntity.notFound().build();
        CurrentUser.requireSelf(authentication, trip.get().getUserId());
        this.tripCommandService.handle(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
