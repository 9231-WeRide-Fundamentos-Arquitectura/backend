package org.example.backendweride.platform.garage.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Existing vehicle to save as a favorite for the authenticated user, with optional personal notes.")
public record CreateFavoriteResource(
        @Schema(description = "Positive ID of an existing vehicle", example = "1") @NotNull @Positive Long vehicleId,
        @Schema(description = "Optional account ID; must match the authenticated account", example = "1") String userId,
        @Schema(description = "Optional personal notes, up to 500 characters", maxLength = 500) @Size(max = 500) String notes) {}
