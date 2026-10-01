package org.example.backendweride.platform.booking.domain.services;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.StartRideCommand;
import org.example.backendweride.platform.booking.domain.model.commands.CancelBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.DeleteBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.CompleteBookingCommand; // <--- Import nuevo

import java.util.Optional;

public interface BookingCommandService {
    Optional<Booking> handle(CreateBookingCommand command);
    Optional<Booking> handle(StartRideCommand command);
    Optional<Booking> handle(CompleteBookingCommand command);
    Optional<Booking> handle(CancelBookingCommand command);
    void handle(DeleteBookingCommand command);
    Optional<Booking> handle(org.example.backendweride.platform.booking.domain.model.commands.RateBookingCommand command);
}
