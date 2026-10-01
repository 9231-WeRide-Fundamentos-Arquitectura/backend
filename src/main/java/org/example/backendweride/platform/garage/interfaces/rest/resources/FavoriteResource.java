package org.example.backendweride.platform.garage.interfaces.rest.resources;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Personal saved vehicle. The favorite ID is distinct from its vehicle ID.")
public record FavoriteResource(
        @Schema(description = "Favorite ID used when deleting the favorite", example = "1") String id,
        @Schema(description = "Owner's account ID", example = "1") String userId,
        @Schema(description = "Saved vehicle ID", example = "7") String vehicleId,
        @Schema(description = "Server timestamp at which the favorite was saved") Instant addedAt,
        @Schema(description = "Optional personal notes", maxLength = 500) String notes) {}
