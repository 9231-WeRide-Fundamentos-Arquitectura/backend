package org.example.backendweride.platform.booking.application.commandservices;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.commands.CompleteBookingCommand; // <--- Importante
import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.StartRideCommand;
import org.example.backendweride.platform.booking.domain.model.commands.CancelBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.DeleteBookingCommand;
import org.example.backendweride.platform.booking.domain.model.valueobjects.Rating; // <--- Importante
import org.example.backendweride.platform.booking.domain.services.BookingCommandService;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@Service
public class BookingCommandServiceImpl implements BookingCommandService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final AccountRepository accountRepository;

    public BookingCommandServiceImpl(BookingRepository bookingRepository, VehicleRepository vehicleRepository, AccountRepository accountRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.accountRepository = accountRepository;
    }

    // 1. Crear Reserva
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Optional<Booking> handle(CreateBookingCommand command) {
        // Serialize reservation creation with account deletion, so an active booking cannot appear after its check.
        final long ownerId;
        try { ownerId = Long.parseLong(command.userId()); }
        catch (NumberFormatException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user id"); }
        accountRepository.findLockedById(ownerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        final long vehicleId;
        try {
            vehicleId = Long.parseLong(command.vehicleId());
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vehicle id");
        }
        var vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
        if ("electric_scooter".equals(vehicle.getType()) && vehicle.getBattery() != null && vehicle.getBattery() < 15)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Batería crítica");
        var booking = new Booking(command);
        // Choque con la reserva de cualquier usuario sobre el mismo vehículo (US22 esc. 2).
        var from = booking.busyFrom();
        var to = command.endDate() != null ? command.endDate() : new Date(from.getTime() + 60_000L);
        if (bookingRepository.findAllByVehicleIdAndStatusIn(command.vehicleId(), Booking.ACTIVE_STATUSES)
                .stream().anyMatch(b -> b.overlaps(from, to)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle is not available in that time range");
        bookingRepository.save(booking);
        return Optional.of(booking);
    }

    // 2. Iniciar Viaje
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Optional<Booking> handle(StartRideCommand command) {
        var bookingOptional = bookingRepository.findLockedById(command.bookingId());

        if (bookingOptional.isPresent()) {
            var booking = bookingOptional.get();
            booking.startRide();
            bookingRepository.save(booking);
            return Optional.of(booking);
        }

        return Optional.empty();
    }


    @Override
    @org.springframework.transaction.annotation.Transactional
    public Optional<Booking> handle(CompleteBookingCommand command) {
        var bookingOptional = bookingRepository.findLockedById(command.bookingId());

        if (bookingOptional.isPresent()) {
            var booking = bookingOptional.get();


            var rating = command.ratingScore() == null ? null : new Rating(command.ratingScore(), command.ratingComment());


            booking.completeBooking(
                    command.totalCost(),
                    command.discount(),
                    command.distance(),
                    command.duration(),
                    command.averageSpeed(),
                    rating,
                    command.routeCoordinates()
            );
            booking.markRouteSource(command.routeSource());

            // Guardamos los cambios
            bookingRepository.save(booking);
            return Optional.of(booking);
        }

        return Optional.empty();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Optional<Booking> handle(CancelBookingCommand command) {
        return bookingRepository.findLockedById(command.bookingId()).map(booking -> {
            booking.cancel();
            return bookingRepository.save(booking);
        });
    }

    @Override
    public void handle(DeleteBookingCommand command) {
        bookingRepository.deleteById(command.bookingId());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Optional<Booking> handle(org.example.backendweride.platform.booking.domain.model.commands.RateBookingCommand command) {
        return bookingRepository.findLockedById(command.bookingId()).map(booking -> {
            if (!booking.getUserId().equals(command.userId()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resource belongs to another user");
            booking.rate(new Rating(command.score(), command.comment(), command.tags()));
            return bookingRepository.save(booking);
        });
    }
}
