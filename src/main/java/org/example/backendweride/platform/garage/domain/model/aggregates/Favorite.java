package org.example.backendweride.platform.garage.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendweride.platform.garage.domain.model.commands.CreateFavoriteCommand;
import java.time.Instant;

@Entity
@Getter
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "vehicle_id"}))
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;
    @Column(nullable = false)
    private Instant addedAt;
    @Column(length = 500)
    private String notes;

    protected Favorite() {}

    public Favorite(CreateFavoriteCommand command) {
        userId = command.userId();
        vehicleId = command.vehicleId();
        notes = command.notes();
        addedAt = Instant.now();
    }
}
