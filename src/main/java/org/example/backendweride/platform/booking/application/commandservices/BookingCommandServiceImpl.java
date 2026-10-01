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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@Service
public class BookingCommandServiceImpl implements BookingCommandService {

    private final BookingRepository bookingRepository;

    public BookingCommandServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // 1. Crear Reserva
    @Override
    public Optional<Booking> handle(CreateBookingCommand command) {
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
    public Optional<Booking> handle(StartRideCommand command) {
        var bookingOptional = bookingRepository.findById(command.bookingId());

        if (bookingOptional.isPresent()) {
            var booking = bookingOptional.get();
            booking.startRide();
            bookingRepository.save(booking);
            return Optional.of(booking);
        }

        return Optional.empty();
    }


    @Override
    public Optional<Booking> handle(CompleteBookingCommand command) {
        var bookingOptional = bookingRepository.findById(command.bookingId());

        if (bookingOptional.isPresent()) {
            var booking = bookingOptional.get();


            var rating = new Rating(command.ratingScore(), command.ratingComment());


            booking.completeBooking(
                    command.totalCost(),
                    command.discount(),
                    command.distance(),
                    command.duration(),
                    command.averageSpeed(),
                    rating
            );

            // Guardamos los cambios
            bookingRepository.save(booking);
            return Optional.of(booking);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Booking> handle(CancelBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            booking.cancel();
            return bookingRepository.save(booking);
        });
    }

    @Override
    public void handle(DeleteBookingCommand command) {
        bookingRepository.deleteById(command.bookingId());
    }
}
