package org.example.backendweride.platform.booking.interfaces;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.commands.CancelBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.DeleteBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.StartRideCommand;
import org.example.backendweride.platform.booking.domain.model.queries.GetAllBookingsByUserIdQuery;
import org.example.backendweride.platform.booking.domain.model.queries.GetBookingByIdQuery;
import org.example.backendweride.platform.booking.domain.services.BookingCommandService;
import org.example.backendweride.platform.booking.domain.services.BookingQueryService;
import org.example.backendweride.platform.booking.interfaces.resources.BookingResource;
import org.example.backendweride.platform.booking.interfaces.resources.CompleteBookingResource;
import org.example.backendweride.platform.booking.interfaces.resources.CreateBookingResource;
import org.example.backendweride.platform.booking.interfaces.transform.BookingResourceFromEntityAssembler;
import org.example.backendweride.platform.booking.interfaces.transform.CompleteBookingCommandFromResourceAssembler;
import org.example.backendweride.platform.booking.interfaces.transform.CreateBookingCommandFromResourceAssembler;
import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @PutMapping("/{bookingId}/start")
    public ResponseEntity<BookingResource> startRide(@PathVariable String bookingId, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        var booking = bookingCommandService.handle(new StartRideCommand(bookingId));

        if (booking.isEmpty()) return ResponseEntity.notFound().build();

        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking.get());
        return ResponseEntity.ok(resource);
    }

    // 3. POST: Completar Viaje
    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResource> completeBooking(@PathVariable String bookingId, @RequestBody CompleteBookingResource resource, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        var command = CompleteBookingCommandFromResourceAssembler.toCommandFromResource(bookingId, resource);
        var booking = bookingCommandService.handle(command);

        if (booking.isEmpty()) return ResponseEntity.notFound().build();

        var bookingResource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking.get());
        return ResponseEntity.ok(bookingResource);
    }

    // 4. GET: Listar reservas del usuario autenticado (filtro opcional por vehículo)
    @GetMapping
    public ResponseEntity<List<BookingResource>> getAllBookings(@RequestParam(required = false) String vehicleId, Authentication authentication) {
        var userId = String.valueOf(CurrentUser.id(authentication));
        var resources = bookingQueryService.handle(new GetAllBookingsByUserIdQuery(userId)).stream()
                .filter(b -> vehicleId == null || vehicleId.equals(b.getVehicleId()))
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    // 5. DELETE: Borrar reserva
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String bookingId, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        bookingCommandService.handle(new DeleteBookingCommand(bookingId));
        return ResponseEntity.noContent().build();
    }

    // 6. PATCH: Cancelar reserva
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResource> cancelBooking(@PathVariable String bookingId, Authentication authentication) {
        if (ownedBooking(bookingId, authentication).isEmpty()) return ResponseEntity.notFound().build();
        return bookingCommandService.handle(new CancelBookingCommand(bookingId))
                .map(b -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
