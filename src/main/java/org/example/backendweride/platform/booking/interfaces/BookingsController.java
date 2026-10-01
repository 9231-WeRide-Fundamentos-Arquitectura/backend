package org.example.backendweride.platform.booking.interfaces;

import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.commands.CancelBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.DeleteBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.StartRideCommand;
import org.example.backendweride.platform.booking.domain.model.queries.GetActiveBookingsByVehicleIdQuery;
import org.example.backendweride.platform.booking.domain.model.queries.GetAllBookingsByUserIdQuery;
import org.example.backendweride.platform.booking.domain.model.queries.GetBookingByIdQuery;
import org.example.backendweride.platform.booking.domain.services.BookingCommandService;
import org.example.backendweride.platform.booking.domain.services.BookingQueryService;
import org.example.backendweride.platform.booking.interfaces.resources.BookingResource;
import org.example.backendweride.platform.booking.interfaces.resources.CompleteBookingResource;
import org.example.backendweride.platform.booking.interfaces.resources.CreateBookingResource;
import org.example.backendweride.platform.booking.interfaces.resources.VehicleAvailabilityResource;
import org.example.backendweride.platform.booking.interfaces.transform.BookingResourceFromEntityAssembler;
import org.example.backendweride.platform.booking.interfaces.transform.CompleteBookingCommandFromResourceAssembler;
import org.example.backendweride.platform.booking.interfaces.transform.CreateBookingCommandFromResourceAssembler;
import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Tag(name = "Bookings", description = "Reserve vehicles and manage the booking lifecycle: create, start, complete, cancel, list and delete bookings of the authenticated user.")
@RestController
@RequestMapping(value = "/api/v1/bookings")
public class BookingsController {

    private final BookingCommandService bookingCommandService;
    private final BookingQueryService bookingQueryService;

    public BookingsController(BookingCommandService bookingCommandService, BookingQueryService bookingQueryService) {
        this.bookingCommandService = bookingCommandService;
        this.bookingQueryService = bookingQueryService;
    }

    /** Finds a booking and checks it belongs to the caller; empty means 404. */
    private Optional<Booking> ownedBooking(String bookingId, Authentication authentication) {
        var booking = bookingQueryService.handle(new GetBookingByIdQuery(bookingId));
        booking.ifPresent(b -> CurrentUser.requireSelf(authentication, b.getUserId()));
        return booking;
    }

    // 1. POST: Crear Reserva
    @Operation(summary = "Create a booking", description = "Reserve a vehicle for the authenticated user between a start and an end location. The userId in the body must match the authenticated account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "409", description = "Critical scooter battery below 15 percent or vehicle already reserved in that time range"),
            @ApiResponse(responseCode = "400", description = "Booking could not be created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @PostMapping
    public ResponseEntity<BookingResource> createBooking(@RequestBody CreateBookingResource resource, Authentication authentication) {
        CurrentUser.requireSelf(authentication, resource.userId());
        var command = CreateBookingCommandFromResourceAssembler.toCommandFromResource(resource);
        var booking = bookingCommandService.handle(command);

        if (booking.isEmpty()) return ResponseEntity.badRequest().build();

        var bookingResource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking.get());
        return new ResponseEntity<>(bookingResource, HttpStatus.CREATED);
    }

    // 2. PUT: Iniciar Viaje
    @Operation(summary = "Start a ride", description = "Mark the booking as started so the ride begins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride started successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @PutMapping("/{bookingId}/start")
    public ResponseEntity<BookingResource> startRide(@Parameter(description = "Unique identifier of the booking") @PathVariable String bookingId, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        var booking = bookingCommandService.handle(new StartRideCommand(bookingId));

        if (booking.isEmpty()) return ResponseEntity.notFound().build();

        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking.get());
        return ResponseEntity.ok(resource);
    }

    // 3. POST: Completar Viaje
    @Operation(summary = "Complete a ride", description = "Finish the booking and atomically persist cost, distance, duration, average speed, an optional rating and up to 5000 observed GPS coordinates. Missing routeCoordinates stores an empty route.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking completed successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResource> completeBooking(@Parameter(description = "Unique identifier of the booking") @PathVariable String bookingId, @RequestBody CompleteBookingResource resource, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        var command = CompleteBookingCommandFromResourceAssembler.toCommandFromResource(bookingId, resource);
        var booking = bookingCommandService.handle(command);

        if (booking.isEmpty()) return ResponseEntity.notFound().build();

        var bookingResource = BookingResourceFromEntityAssembler.toResourceWithRoute(booking.get());
        return ResponseEntity.ok(bookingResource);
    }

    // 4. GET: Listar reservas del usuario autenticado (filtro opcional por vehículo)
    @Operation(summary = "Rate a completed booking", description = "Owner only. Score 1–5, optional comment up to 1000 characters and fixed tags. Returns 409 if not completed or already rated; 400 for invalid input.")
    @PostMapping("/{bookingId}/rating")
    public ResponseEntity<BookingResource> rateBooking(@PathVariable String bookingId,
            @RequestBody org.example.backendweride.platform.booking.interfaces.resources.RateBookingResource resource,
            Authentication authentication) {
        return bookingCommandService.handle(new org.example.backendweride.platform.booking.domain.model.commands.RateBookingCommand(
                bookingId, String.valueOf(CurrentUser.id(authentication)), resource.score(), resource.comment(), resource.tags()))
                .map(b -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get my bookings", description = "List the bookings of the authenticated user, optionally filtered by vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public ResponseEntity<List<BookingResource>> getAllBookings(@Parameter(description = "Optional vehicle ID used to filter the bookings") @RequestParam(required = false) String vehicleId, Authentication authentication) {
        var userId = String.valueOf(CurrentUser.id(authentication));
        var resources = bookingQueryService.handle(new GetAllBookingsByUserIdQuery(userId)).stream()
                .filter(b -> vehicleId == null || vehicleId.equals(b.getVehicleId()))
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get my trip history", description = "Page completed bookings owned by the authenticated account, including persisted metrics, final cost and recorded route. Old rides remain visible with an empty route.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "History page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid page or size"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")})
    @GetMapping("/history")
    public org.example.backendweride.platform.booking.interfaces.resources.BookingHistoryResource getHistory(
            @Parameter(description = "Zero based page", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size from 1 to 100", example = "10") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        var history = bookingQueryService.handle(new org.example.backendweride.platform.booking.domain.model.queries.GetBookingHistoryByUserIdQuery(
                String.valueOf(CurrentUser.id(authentication)), page, size));
        return new org.example.backendweride.platform.booking.interfaces.resources.BookingHistoryResource(
                history.getContent().stream().map(BookingResourceFromEntityAssembler::toResourceFromEntity).toList(),
                history.getTotalElements(), history.getTotalPages(), history.getNumber(), history.getSize());
    }

    @Operation(summary = "Get a completed ride", description = "Return one completed booking owned by the authenticated account with its recorded route and ride metrics.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Completed ride returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Ride belongs to another account"),
            @ApiResponse(responseCode = "404", description = "Completed ride not found")})
    @GetMapping("/history/{bookingId}")
    public ResponseEntity<BookingResource> getHistoryDetail(
            @Parameter(description = "Completed booking UUID") @PathVariable String bookingId, Authentication authentication) {
        return ownedBooking(bookingId, authentication).filter(booking -> "completed".equals(booking.getStatus()))
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceWithRoute(booking)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a booking", description = "Return one booking owned by the authenticated user, without the recorded route (use the history detail for that).")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Booking returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Booking belongs to another account"),
            @ApiResponse(responseCode = "404", description = "Booking not found")})
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResource> getBooking(@Parameter(description = "Unique identifier of the booking") @PathVariable String bookingId, Authentication authentication) {
        return ownedBooking(bookingId, authentication)
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(booking)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4b. GET: Disponibilidad de un vehículo considerando las reservas de todos los usuarios
    @Operation(summary = "Check vehicle availability", description = "Tell whether a vehicle is free between start and end (ISO-8601 instants) and list its busy slots. Slots are anonymous.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability computed"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping("/availability")
    public ResponseEntity<VehicleAvailabilityResource> getAvailability(@RequestParam String vehicleId, @RequestParam Instant start, @RequestParam Instant end) {
        var from = Date.from(start);
        var to = Date.from(end);
        var busy = bookingQueryService.handle(new GetActiveBookingsByVehicleIdQuery(vehicleId)).stream()
                .filter(b -> b.busyUntil().after(new Date()))
                .sorted(Comparator.comparing(Booking::busyFrom))
                .toList();
        var slots = busy.stream().map(b -> new VehicleAvailabilityResource.BusySlot(b.busyFrom(), b.busyUntil())).toList();
        return ResponseEntity.ok(new VehicleAvailabilityResource(busy.stream().noneMatch(b -> b.overlaps(from, to)), slots));
    }

    // 5. DELETE: Borrar reserva
    @Operation(summary = "Delete a booking", description = "Permanently delete a booking of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@Parameter(description = "Unique identifier of the booking") @PathVariable String bookingId, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        bookingCommandService.handle(new DeleteBookingCommand(bookingId));
        return ResponseEntity.noContent().build();
    }

    // 6. PATCH: Cancelar reserva
    @Operation(summary = "Cancel a booking", description = "Cancel a booking of the authenticated user without deleting it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResource> cancelBooking(@Parameter(description = "Unique identifier of the booking") @PathVariable String bookingId, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        return bookingCommandService.handle(new CancelBookingCommand(bookingId))
                .map(b -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
