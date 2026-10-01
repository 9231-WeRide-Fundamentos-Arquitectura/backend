package org.example.backendweride.platform.unlock;

import org.example.backendweride.platform.unlock.domain.model.UnlockRequest;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class UnlockRequestTest {
    @Test
    void codesAreBoundToTheVehicleAndFailedRequestsCanRetry() {
        var request = new UnlockRequest("42", "7", "booking-id", Instant.now(), "qr_code");
        assertEquals("pending", request.getStatus());
        request.attempt("qr_code", "weride:vehicle:8", "XM007");
        assertEquals("failed", request.getStatus());
        assertNull(request.getActualUnlockTime());
        request.attempt("manual", "WRONG", "XM007");
        assertEquals("failed", request.getStatus());
        request.attempt("manual", " xm007 ", "XM007");
        assertEquals("unlocked", request.getStatus());
        assertNotNull(request.getActualUnlockTime());
        assertEquals(3, request.getAttempts());
        assertNull(request.getErrorMessage());
        var qr = new UnlockRequest("42", "7", "booking-id", Instant.now(), "qr_code");
        qr.attempt("qr_code", "weride:vehicle:7", "XM007");
        assertEquals("unlocked", qr.getStatus());
    }
}
