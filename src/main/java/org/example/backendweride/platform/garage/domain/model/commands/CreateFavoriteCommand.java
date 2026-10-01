package org.example.backendweride.platform.garage.domain.model.commands;

public record CreateFavoriteCommand(Long userId, Long vehicleId, String notes) {}
