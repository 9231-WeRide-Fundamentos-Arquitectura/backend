package org.example.backendweride.platform.booking.application.commandservices;

import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingBatteryTest {
    @Test
    void criticalScootersCannotBeReservedNowOrLaterButFifteenPercentAndBikesAreAllowed() {
        var bookings = mock(BookingRepository.class);
        var vehicles = mock(VehicleRepository.class);
        var vehicle = mock(Vehicle.class);
        when(vehicles.findById(1L)).thenReturn(Optional.of(vehicle));
        when(bookings.findAllByVehicleIdAndStatusIn(anyString(), anyList())).thenReturn(List.of());
        when(vehicle.getType()).thenReturn("electric_scooter");
        var accounts = mock(org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository.class);
        when(accounts.findLockedById(7L)).thenReturn(Optional.of(new org.example.backendweride.platform.iam.domain.model.aggregates.Account("test@example.com", "hash")));
        var service = new BookingCommandServiceImpl(bookings, vehicles, accounts);
        for (int battery : List.of(0, 14)) {
            when(vehicle.getBattery()).thenReturn(battery);
            for (Date start : new Date[] {null, new Date(System.currentTimeMillis() + 3600000)}) {
                var error = assertThrows(ResponseStatusException.class, () -> service.handle(
                        new CreateBookingCommand("7", "1", "1", "1", start, null, null)));
                assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
                assertEquals("Batería crítica", error.getReason());
            }
        }
        verify(bookings, never()).save(any());
        when(vehicle.getBattery()).thenReturn(15);
        assertTrue(service.handle(new CreateBookingCommand("7", "1", "1", "1", null, null, null)).isPresent());
        when(vehicle.getType()).thenReturn("bike");
        when(vehicle.getBattery()).thenReturn(0);
        assertTrue(service.handle(new CreateBookingCommand("7", "1", "1", "1", null, null, null)).isPresent());
    }
}
