package org.example.backendweride.platform.trip.domain.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record RouteCoordinates(double lat, double lng) {
}
