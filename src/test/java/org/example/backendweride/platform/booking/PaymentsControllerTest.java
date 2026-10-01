package org.example.backendweride.platform.booking;

import org.example.backendweride.platform.booking.interfaces.PaymentsController;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.example.backendweride.platform.iam.infrastructure.auth.model.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentsControllerTest {
    @Test void summaryUsesAuthenticatedOwnerAndRejectsAnotherAccount() {
        var bookings = mock(BookingRepository.class);
        var controller = new PaymentsController(bookings);
        var user = new UserDetailsImpl(42L, "qa@example.com", "", List.of());
        var authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());
        when(bookings.totalPaid("42")).thenReturn(1250.0);
        assertEquals(1250.0, controller.summary(authentication, null).totalSpent());
        assertEquals("USD", controller.summary(authentication, "42").currency());
        var error = assertThrows(ResponseStatusException.class, () -> controller.summary(authentication, "43"));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(bookings, never()).totalPaid("43");
        assertThrows(ResponseStatusException.class, () -> controller.list(authentication, null, -1, 10));
    }
}
