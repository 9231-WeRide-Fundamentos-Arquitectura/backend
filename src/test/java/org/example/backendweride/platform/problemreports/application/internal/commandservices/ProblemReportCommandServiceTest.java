package org.example.backendweride.platform.problemreports.application.internal.commandservices;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.problemreports.domain.model.aggregates.ProblemReport;
import org.example.backendweride.platform.problemreports.domain.model.commands.CreateProblemReportCommand;
import org.example.backendweride.platform.problemreports.infrastructure.persistence.jpa.ProblemReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProblemReportCommandServiceTest {
    @Test
    void initialFaultWaivesChargeButLaterFaultAndOtherOwnersDoNot() {
        var entities = mock(EntityManager.class);
        var reports = mock(ProblemReportRepository.class);
        var booking = mock(Booking.class);
        var vehicle = mock(Vehicle.class);
        when(entities.find(Vehicle.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(vehicle);
        when(entities.find(Booking.class, "booking", LockModeType.PESSIMISTIC_WRITE)).thenReturn(booking);
        when(reports.save(any(ProblemReport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(booking.getUserId()).thenReturn("7");
        when(booking.getVehicleId()).thenReturn("1");
        when(booking.getStatus()).thenReturn("in_progress");
        when(booking.getActualStartDate()).thenReturn(new Date(System.currentTimeMillis() - 10_000));
        var service = new ProblemReportCommandService(entities, reports);
        var command = new CreateProblemReportCommand("7", "1", "booking", List.of("brakes"), "Broken brake", null);
        assertTrue(service.handle(command).isChargeWaived());
        verify(booking).completeBooking(0.0, 0.0, 0.0, 0, 0.0, null);
        verify(booking).markChargeWaived();
        verify(vehicle).markForMaintenance();
        clearInvocations(booking);
        when(booking.getActualStartDate()).thenReturn(new Date(System.currentTimeMillis() - 61_000));
        assertFalse(service.handle(command).isChargeWaived());
        verify(booking, never()).completeBooking(anyDouble(), anyDouble(), anyDouble(), anyInt(), anyDouble(), any());
        when(booking.getUserId()).thenReturn("8");
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.handle(command)).getStatusCode().value());
    }

    @Test
    void reportNeedsAnOwnActiveOrRecentlyCompletedBookingOfThatVehicle() {
        var entities = mock(EntityManager.class);
        var reports = mock(ProblemReportRepository.class);
        var booking = mock(Booking.class);
        var vehicle = mock(Vehicle.class);
        when(entities.find(Vehicle.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(vehicle);
        when(entities.find(Booking.class, "booking", LockModeType.PESSIMISTIC_WRITE)).thenReturn(booking);
        when(reports.save(any(ProblemReport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(booking.getUserId()).thenReturn("7");
        when(booking.getVehicleId()).thenReturn("1");
        var service = new ProblemReportCommandService(entities, reports);
        var without = new CreateProblemReportCommand("7", "1", null, List.of("brakes"), "x", null);
        var with = new CreateProblemReportCommand("7", "1", "booking", List.of("brakes"), "x", null);
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.handle(without)).getStatusCode().value());
        when(booking.getStatus()).thenReturn("cancelled");
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.handle(with)).getStatusCode().value());
        when(booking.getStatus()).thenReturn("completed");
        when(booking.getActualEndDate()).thenReturn(new Date(System.currentTimeMillis() - 2 * 3_600_000L));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.handle(with)).getStatusCode().value());
        verify(vehicle, never()).markForMaintenance();
        when(booking.getActualEndDate()).thenReturn(new Date(System.currentTimeMillis() - 60_000));
        assertFalse(service.handle(with).isChargeWaived());
        verify(vehicle).markForMaintenance();
    }

    @Test
    void rejectsInvalidCategoryAndPhotoWithoutTouchingVehicle() {
        var entities = mock(EntityManager.class);
        var service = new ProblemReportCommandService(entities, mock(ProblemReportRepository.class));
        assertThrows(ResponseStatusException.class, () -> service.handle(new CreateProblemReportCommand(
                "7", "1", null, List.of("unknown"), "", null)));
        assertThrows(ResponseStatusException.class, () -> ProblemReportCommandService.validatePhoto("data:image/png;base64,dGV4dA=="));
        assertThrows(ResponseStatusException.class, () -> ProblemReportCommandService.validatePhoto("data:image/svg+xml;base64,dGV4dA=="));
        assertDoesNotThrow(() -> ProblemReportCommandService.validatePhoto(null));
        verifyNoInteractions(entities);
    }
}
