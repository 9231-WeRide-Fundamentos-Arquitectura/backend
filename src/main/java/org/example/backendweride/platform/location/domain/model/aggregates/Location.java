package org.example.backendweride.platform.location.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendweride.platform.location.domain.commands.CreateLocationCommand;
import org.example.backendweride.platform.location.domain.valueobjects.Coordinates;
import org.example.backendweride.platform.location.domain.valueobjects.OperatingHours;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private String name;
    @Getter
    private String address;

    @Embedded
    @Getter
    private Coordinates coordinates;

    @Getter
    private String type;
    @Getter
    private int capacity;
    @Getter
    private int availableSpots;
    @Getter
    private boolean isActive;

    @Embedded
    @Getter
    private OperatingHours operatingHours;
    @Getter
    private List<String> amenities = new ArrayList<>();
    @Getter
    private String district;

    @Column(columnDefinition = "TEXT")
    @Getter
    private String description;
    @Getter
    private String image;

    protected Location() {
    }

    public Location(CreateLocationCommand locationCommand){
        this.name = locationCommand.name();
        this.address = locationCommand.address();
        this.coordinates = locationCommand.coordinates();
        this.type = locationCommand.type();
        this.capacity = locationCommand.capacity();
        this.availableSpots = locationCommand.availableSpots();
        this.isActive = locationCommand.isActive();
        this.operatingHours = locationCommand.operatingHours();
        this.amenities = locationCommand.amenities();
        this.district = locationCommand.district();
        this.description = locationCommand.description();
        this.image = locationCommand.image();
    }
}
