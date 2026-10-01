package org.example.backendweride.platform.booking.domain.model.aggregates;

import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.domain.model.valueobjects.Rating;
import jakarta.persistence.*;
import org.example.backendweride.platform.booking.domain.model.valueobjects.TripRouteCoordinate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<TripRouteCoordinate> routeCoordinates = List.of();

    private String routeSource = "gps";

    public List<TripRouteCoordinate> getRouteCoordinates() {
        return routeCoordinates == null ? List.of() : List.copyOf(routeCoordinates);
    }

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

    public void anonymize(String anonymousOwner) {
        if (ACTIVE_STATUSES.contains(status) || "paused".equals(status))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "ACTIVE_BOOKINGS");
        userId = anonymousOwner;
        rating = null;
        routeCoordinates = List.of();
    }

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
        if (!"reserved".equals(status) || (startDate != null && startDate.after(new Date())))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Booking cannot start yet");
        this.status = "in_progress";
        this.actualStartDate = new Date();
    }

    public void cancel() {
        if ("completed".equals(status) || "cancelled".equals(status))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Booking cannot be cancelled");
        this.status = "cancelled";
    }

    /** "simulated" marks a route interpolated between stations because no valid GPS fix was recorded. */
    public void markRouteSource(String source) {
        if (source == null || source.isBlank()) { this.routeSource = "gps"; return; }
        if (!"gps".equals(source) && !"simulated".equals(source))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid route source");
        this.routeSource = source;
    }

    public void markChargeWaived() {
        this.paymentStatus = "waived";
    }

    public void rate(Rating rating) {
        if (!"completed".equals(status) || (this.rating != null && this.rating.getScore() != null))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Booking must be completed and not already rated");
        this.rating = rating;
    }

    // MÉTODO: Para finalizar la reserva (Resuelve advertencia de completeBooking)
    public void completeBooking(Double totalCost, Double discount, Double distance, Integer duration, Double averageSpeed, Rating rating) {
        completeBooking(totalCost, discount, distance, duration, averageSpeed, rating, List.of());
    }

    public void completeBooking(Double totalCost, Double discount, Double distance, Integer duration, Double averageSpeed,
                                Rating rating, List<TripRouteCoordinate> routeCoordinates) {
        var route = routeCoordinates == null ? List.<TripRouteCoordinate>of() : routeCoordinates;
        if (route.size() > 5000 || route.stream().anyMatch(java.util.Objects::isNull))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Route must contain at most 5000 valid coordinates");
        if (!"in_progress".equals(status))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Booking is not in progress");
        if (totalCost == null || discount == null || distance == null || duration == null || averageSpeed == null
                || !Double.isFinite(totalCost) || !Double.isFinite(discount) || !Double.isFinite(distance) || !Double.isFinite(averageSpeed)
                || totalCost < 0 || discount < 0 || discount > totalCost || distance < 0 || duration < 0 || averageSpeed < 0)
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid trip metrics or cost");
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
        this.routeCoordinates = List.copyOf(route);
    }
}
