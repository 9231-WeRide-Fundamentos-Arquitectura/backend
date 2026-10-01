package org.example.backendweride.platform.unlock.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "unlock_requests")
@Getter
@NoArgsConstructor
public class UnlockRequest {
    @Id private String id;
    private String userId;
    private String vehicleId;
    private String bookingId;
    private Instant requestedAt;
    private Instant scheduledUnlockTime;
    private Instant actualUnlockTime;
    private String status;
    private String method;
    private String unlockCode = "";
    private int attempts;
    private String errorMessage;

    public UnlockRequest(String userId, String vehicleId, String bookingId, Instant scheduledUnlockTime, String method) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.bookingId = bookingId;
        this.requestedAt = Instant.now();
        this.scheduledUnlockTime = scheduledUnlockTime;
        this.method = method;
        this.status = "pending";
    }

    public void attempt(String method, String code, String licensePlate) {
        this.method = method;
        this.unlockCode = code;
        this.attempts++;
        boolean valid = "qr_code".equals(method) ? ("weride:vehicle:" + vehicleId).equals(code)
                : "manual".equals(method) && licensePlate != null && licensePlate.equalsIgnoreCase(code.trim());
        this.status = valid ? "unlocked" : "failed";
        this.actualUnlockTime = valid ? Instant.now() : null;
        this.errorMessage = valid ? null : "Código no reconocido. Ingresa la placa del vehículo o reintenta el QR.";
    }
}
