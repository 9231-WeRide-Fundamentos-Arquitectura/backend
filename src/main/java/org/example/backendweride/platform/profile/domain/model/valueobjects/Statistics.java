package org.example.backendweride.platform.profile.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class Statistics {
    private int totalTrips;
    private double totalDistance;
    private double totalSpent;
    private double averageRating;

    public Statistics(int totalTrips, double totalDistance, double totalSpent, double averageRating) {
        this.totalTrips = totalTrips;
        this.totalDistance = totalDistance;
        this.totalSpent = totalSpent;
        this.averageRating = averageRating;
    }

    public static Statistics defaultStatistics() {
        return new Statistics(0, 0.0, 0.0, 0.0);
    }
}