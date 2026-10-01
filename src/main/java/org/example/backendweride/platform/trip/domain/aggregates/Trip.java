package org.example.backendweride.platform.trip.domain.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendweride.platform.trip.domain.commands.CreateTripCommand;
import org.example.backendweride.platform.trip.domain.valueobjects.RouteCoordinates;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private Long bookingId;

    @Getter
    private String userId;

    @Getter
    private Long vehicleId;

    @Getter
    private Long startLocationId;

    @Getter
    private Long endLocationId;

    @Getter
    private String route;
    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    private List<RouteCoordinates> routeCoordinates = new ArrayList<>();
    @Getter
    private Date startDate;
    @Getter
    private Date endDate;
    @Getter
    private int duration;
    @Getter
    private float distance;
    @Getter
    private float averageSpeed;
    @Getter
    private float maxSpeed;
    @Getter
    private float totalCost;
    @Getter
    private float carbonSaved;
    @Getter
    private int caloriesBurned;
    @Getter
    private String weather;
    @Getter
    private int temperature;
    @Getter
    private String status;
    @Getter
    private List<String> incidentReports = new ArrayList<>();
    @Getter
    private List<String> photos = new ArrayList<>();

    protected Trip() {

    }

    public Trip(CreateTripCommand tripCommand) {
        this.userId = tripCommand.userId();
        this.bookingId = tripCommand.bookingId();
        this.vehicleId = tripCommand.vehicleId();
        this.startLocationId = tripCommand.startLocationId();
        this.endLocationId = tripCommand.endLocationId();
        this.route = tripCommand.route();
        this.routeCoordinates = tripCommand.routeCoordinates();
        this.startDate = tripCommand.startDate();
        this.endDate = tripCommand.endDate();
        this.duration = tripCommand.duration();
        this.distance = tripCommand.distance();
        this.averageSpeed = tripCommand.averageSpeed();
        this.maxSpeed = tripCommand.maxSpeed();
        this.totalCost = tripCommand.totalCost();
        this.carbonSaved = tripCommand.carbonSaved();
        this.caloriesBurned = tripCommand.caloriesBurned();
        this.weather = tripCommand.weather();
        this.temperature = tripCommand.temperature();
        this.status = tripCommand.status();
        this.incidentReports = tripCommand.incidentReports();
        this.photos = tripCommand.photos();
    }
 }
