package org.example.backendweride.platform.booking.application.queryservices;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.queries.GetActiveBookingsByVehicleIdQuery;
import org.example.backendweride.platform.booking.domain.model.queries.GetAllBookingsByUserIdQuery;
import org.example.backendweride.platform.booking.domain.model.queries.GetBookingByIdQuery;
import org.example.backendweride.platform.booking.domain.services.BookingQueryService;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingQueryServiceImpl implements BookingQueryService {

    private final BookingRepository bookingRepository;

    public BookingQueryServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public org.springframework.data.domain.Page<Booking> handle(org.example.backendweride.platform.booking.domain.model.queries.GetBookingHistoryByUserIdQuery query) {
        if (query.page() < 0 || query.size() < 1 || query.size() > 100)
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid history page");
        return bookingRepository.findByUserIdAndStatus(query.userId(), "completed", org.springframework.data.domain.PageRequest.of(
                query.page(), query.size(), org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "actualEndDate", "id")));
    }

    @Override
    public Optional<Booking> handle(GetBookingByIdQuery query) {
        return bookingRepository.findById(query.bookingId());
    }

    @Override
    public List<Booking> handle(GetAllBookingsByUserIdQuery query) {
        return bookingRepository.findAllByUserId(query.userId());
    }

    @Override
    public List<Booking> handle(GetActiveBookingsByVehicleIdQuery query) {
        return bookingRepository.findAllByVehicleIdAndStatusIn(query.vehicleId(), Booking.ACTIVE_STATUSES);
    }
}