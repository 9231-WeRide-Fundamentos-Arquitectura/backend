package org.example.backendweride.platform.booking.domain.services;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.queries.GetActiveBookingsByVehicleIdQuery;
import org.example.backendweride.platform.booking.domain.model.queries.GetAllBookingsByUserIdQuery;
import org.example.backendweride.platform.booking.domain.model.queries.GetBookingByIdQuery;

import java.util.List;
import java.util.Optional;

public interface BookingQueryService {
    org.springframework.data.domain.Page<Booking> handle(org.example.backendweride.platform.booking.domain.model.queries.GetBookingHistoryByUserIdQuery query);
    Optional<Booking> handle(GetBookingByIdQuery query);
    List<Booking> handle(GetAllBookingsByUserIdQuery query);
    List<Booking> handle(GetActiveBookingsByVehicleIdQuery query);
}