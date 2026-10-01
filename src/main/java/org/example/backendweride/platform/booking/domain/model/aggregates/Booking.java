package org.example.backendweride.platform.booking.domain.model.aggregates;

import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.domain.model.valueobjects.Rating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Booking {

    @Id
    private String id;

    private String userId;
    private String vehicleId;
    private String startLocationId;
    private String endLocationId;

    private Date reservedAt;

    // **ARREGLADO:** Inicialización nula para quitar las advertencias de compilación
    // Estas fechas se llenarán con los datos del Frontend/Comando.
    private Date startDate = null;
    private Date endDate = null;
    private Date actualStartDate = null;
    private Date actualEndDate = null;

    // Campos inicializados para evitar advertencias y garantizar valor por defecto
    private String status;
    private Double totalCost = 0.0;
    private Double discount = 0.0;
    private Double finalCost = 0.0;
    private String paymentMethod = "card";
    private String paymentStatus = "pending";

    private Double distance = 0.0;
    private Integer duration = 0;
    private Double averageSpeed = 0.0;

    @Embedded
    private Rating rating;

    // CONSTRUCTOR: Se usa al crear la reserva (CreateBookingCommand)
    public Booking(CreateBookingCommand command) {
        this.id = UUID.randomUUID().toString();
        this.userId = command.userId();
        this.vehicleId = command.vehicleId();
        this.startLocationId = command.startLocationId();
        this.endLocationId = command.endLocationId();
        this.reservedAt = new Date();
        this.status = "reserved";
        this.startDate = command.startDate();
        this.endDate = command.endDate();
        if (command.totalCost() != null) {
            this.totalCost = command.totalCost();
            this.finalCost = command.totalCost();
        }

        // El resto de campos nulos/cero se inicializan en la declaración del campo.
    }

    public static final List<String> ACTIVE_STATUSES = List.of("reserved", "in_progress");

    /** Window during which this booking keeps its vehicle busy. */
    public Date busyFrom() {
        return startDate != null ? startDate : actualStartDate != null ? actualStartDate : reservedAt;
    }

    // ponytail: reservas inmediatas no tienen endDate; se asumen 2 h. Guardar la duración estimada si hace falta más precisión.
    public Date busyUntil() {
        return endDate != null ? endDate : new Date(busyFrom().getTime() + 2 * 3600_000L);
    }

    public boolean overlaps(Date from, Date to) {
        return busyFrom().before(to) && busyUntil().after(from);
    }

    // MÉTODO: Para iniciar el viaje (Resuelve advertencia de startRide)
    public void startRide() {
        this.status = "in_progress";
        this.actualStartDate = new Date();
    }

    public void cancel() {
        this.status = "cancelled";
    }

    // MÉTODO: Para finalizar la reserva (Resuelve advertencia de completeBooking)
    public void completeBooking(Double totalCost, Double discount, Double distance, Integer duration, Double averageSpeed, Rating rating) {
        this.status = "completed";
        this.paymentStatus = "paid";

        this.totalCost = totalCost;
        this.discount = discount;
        this.finalCost = totalCost - discount;

        this.distance = distance;
        this.duration = duration;
        this.averageSpeed = averageSpeed;

        this.actualEndDate = new Date();
        this.endDate = this.actualEndDate;
        this.rating = rating;
    }
}