package org.example.backendweride.platform.booking;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.domain.model.valueobjects.Rating;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BookingRatingTest {
    @Test void completedBookingAcceptsOneRatingAndCannotBeCompletedAgain() {
        var booking = new Booking(new CreateBookingCommand("1", "1", "1", "1", null, null, null));
        assertThrows(ResponseStatusException.class, () -> booking.rate(new Rating(5, "", List.of())));
        booking.startRide();
        booking.completeBooking(2.0, 0.0, 1.0, 2, 30.0, null);
        assertNull(booking.getRating());
        booking.rate(new Rating(5, "Bien", List.of("clean", "brakes_ok")));
        assertEquals(List.of("clean", "brakes_ok"), booking.getRating().tagList());
        assertThrows(ResponseStatusException.class, () -> booking.rate(new Rating(4, "")));
        assertThrows(ResponseStatusException.class, () -> booking.completeBooking(0.0, 0.0, 0.0, 0, 0.0, null));
        assertEquals(5, booking.getRating().getScore());
    }

    @Test void invalidScoresAndUnknownTagsAreRejected() {
        for (var score : new Integer[]{null, 0, 6}) assertThrows(ResponseStatusException.class, () -> new Rating(score, ""));
        assertThrows(ResponseStatusException.class, () -> new Rating(3, "", List.of("arbitrary")));
        assertThrows(ResponseStatusException.class, () -> new Rating(3, "", java.util.Arrays.asList((String) null)));
        assertThrows(ResponseStatusException.class, () -> new Rating(3, "x".repeat(1001)));
    }
}
