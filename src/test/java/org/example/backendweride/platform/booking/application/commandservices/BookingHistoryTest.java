package org.example.backendweride.platform.booking.application.commandservices;

import org.example.backendweride.platform.booking.application.queryservices.BookingQueryServiceImpl;
import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.commands.CompleteBookingCommand;
import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.domain.model.queries.GetBookingHistoryByUserIdQuery;
import org.example.backendweride.platform.booking.domain.model.valueobjects.TripRouteCoordinate;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.example.backendweride.platform.booking.interfaces.transform.BookingResourceFromEntityAssembler;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingHistoryTest {
    @Test
    void completingRideSavesRouteAndMetricsAndHistoryQueriesOnlyMyCompletedBookings() {
        var repository = mock(BookingRepository.class);
        var booking = new Booking(new CreateBookingCommand("7", "1", "1", "2", null, null, null));
        booking.startRide();
        var route = List.of(new TripRouteCoordinate(-12.1, -77.1), new TripRouteCoordinate(-12.2, -77.2));
        when(repository.findLockedById(booking.getId())).thenReturn(Optional.of(booking));
        var result = new BookingCommandServiceImpl(repository, mock(org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository.class), mock(org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository.class)).handle(new CompleteBookingCommand(
                booking.getId(), 10.0, 2.0, 1.5, 5, 18.0, null, null, route, null)).orElseThrow();
        verify(repository).save(booking);
        assertEquals("completed", result.getStatus());
        assertEquals(route, result.getRouteCoordinates());
        assertEquals(8.0, result.getFinalCost());
        assertEquals(1.5, result.getDistance());
        assertEquals(5, result.getDuration());
        assertEquals(18.0, result.getAverageSpeed());
        assertEquals(route, BookingResourceFromEntityAssembler.toResourceWithRoute(result).routeCoordinates());
        assertNull(BookingResourceFromEntityAssembler.toResourceFromEntity(result).routeCoordinates());
        when(repository.findByUserIdAndStatus(eq("7"), eq("completed"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));
        assertEquals(List.of(booking), new BookingQueryServiceImpl(repository).handle(new GetBookingHistoryByUserIdQuery("7", 0, 10)).getContent());
        verify(repository).findByUserIdAndStatus(eq("7"), eq("completed"), argThat(page -> page.getPageSize() == 10 && page.getPageNumber() == 0));
    }

    @Test
    void missingRouteIsEmptyAndInvalidOrOversizedRouteCannotCompleteRide() {
        var booking = new Booking(new CreateBookingCommand("7", "1", "1", "2", null, null, null));
        assertEquals(List.of(), BookingResourceFromEntityAssembler.toResourceWithRoute(booking).routeCoordinates());
        assertThrows(ResponseStatusException.class, () -> new TripRouteCoordinate(91.0, 0.0));
        assertThrows(ResponseStatusException.class, () -> new TripRouteCoordinate(0.0, Double.NaN));
        booking.startRide();
        assertThrows(ResponseStatusException.class, () -> booking.completeBooking(0.0, 0.0, 0.0, 0, 0.0, null,
                java.util.Collections.nCopies(5001, new TripRouteCoordinate(0.0, 0.0))));
        assertEquals("in_progress", booking.getStatus());
        booking.completeBooking(0.0, 0.0, 0.0, 0, 0.0, null);
        assertEquals(List.of(), booking.getRouteCoordinates());
    }
}
