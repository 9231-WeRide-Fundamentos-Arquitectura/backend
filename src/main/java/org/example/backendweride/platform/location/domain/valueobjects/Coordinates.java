package org.example.backendweride.platform.location.domain.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Coordinates(double lat, double lng) {


}
