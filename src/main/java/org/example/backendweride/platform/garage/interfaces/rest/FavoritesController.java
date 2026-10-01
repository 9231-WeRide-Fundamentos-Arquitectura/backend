package org.example.backendweride.platform.garage.interfaces.rest;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.garage.application.commandservices.FavoriteService;
import org.example.backendweride.platform.garage.domain.model.commands.CreateFavoriteCommand;
import org.example.backendweride.platform.garage.domain.model.commands.DeleteFavoriteCommand;
import org.example.backendweride.platform.garage.domain.model.queries.GetUserFavoritesQuery;
import org.example.backendweride.platform.garage.interfaces.rest.resources.CreateFavoriteResource;
import org.example.backendweride.platform.garage.interfaces.rest.resources.FavoriteResource;
import org.example.backendweride.platform.garage.interfaces.rest.transform.FavoriteResourceFromEntityAssembler;
import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Tag(name = "Favorites", description = "Save vehicles as personal favorites, check whether a vehicle is a favorite and remove saved favorites. All operations use the authenticated user's account.")
@RestController
@RequestMapping("/api/v1/favorites")
public class FavoritesController {
    private final FavoriteService favorites;

    public FavoritesController(FavoriteService favorites) { this.favorites = favorites; }

    @Operation(summary = "Get my favorite vehicles", description = "List the authenticated user's favorites in newest-first order. Returns an empty list when none exist. The optional userId must match the authenticated account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorites retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId filter refers to another account")
    })
    @GetMapping
    public List<FavoriteResource> getFavorites(Authentication authentication, @Parameter(description = "Optional account ID; must match the authenticated user") @RequestParam(required = false) String userId) {
        if (userId != null) CurrentUser.requireSelf(authentication, userId);
        return favorites.handle(new GetUserFavoritesQuery(CurrentUser.id(authentication))).stream()
                .map(FavoriteResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Check whether a vehicle is a favorite", description = "Return true if the authenticated user has saved the given vehicle, otherwise false.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorite status returned as a boolean"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid vehicle ID"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId filter refers to another account")
    })
    @GetMapping("/check")
    public boolean checkFavorite(Authentication authentication, @Parameter(description = "Vehicle ID", example = "1") @RequestParam Long vehicleId,
                                 @Parameter(description = "Optional account ID; must match the authenticated user") @RequestParam(required = false) String userId) {
        if (userId != null) CurrentUser.requireSelf(authentication, userId);
        return favorites.handle(new GetUserFavoritesQuery(CurrentUser.id(authentication))).stream()
                .anyMatch(favorite -> favorite.getVehicleId().equals(vehicleId));
    }

    @Operation(summary = "Save a favorite vehicle", description = "Save an existing vehicle for the authenticated user with optional notes up to 500 characters. A vehicle can be saved only once per account; a repeated request returns the existing favorite, while a concurrent duplicate may return 409. IDs and timestamps are set by the server.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Favorite created or existing favorite returned"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle ID or oversized notes"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId in the body refers to another account"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "409", description = "Concurrent duplicate favorite")
    })
    @PostMapping
    public ResponseEntity<FavoriteResource> createFavorite(Authentication authentication, @Valid @RequestBody CreateFavoriteResource resource) {
        if (resource.userId() != null) CurrentUser.requireSelf(authentication, resource.userId());
        try {
            var favorite = favorites.handle(new CreateFavoriteCommand(CurrentUser.id(authentication), resource.vehicleId(), resource.notes()));
            return ResponseEntity.status(HttpStatus.CREATED).body(FavoriteResourceFromEntityAssembler.toResourceFromEntity(favorite));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle is already a favorite");
        }
    }

    @Operation(summary = "Remove a favorite vehicle", description = "Delete a favorite owned by the authenticated user. The path ID is the favorite ID, not the vehicle ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Favorite removed"),
            @ApiResponse(responseCode = "400", description = "Invalid favorite ID"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Favorite belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Favorite not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(Authentication authentication, @Parameter(description = "Favorite ID", example = "1") @PathVariable Long id) {
        favorites.handle(new DeleteFavoriteCommand(id, CurrentUser.id(authentication)));
        return ResponseEntity.noContent().build();
    }
}
