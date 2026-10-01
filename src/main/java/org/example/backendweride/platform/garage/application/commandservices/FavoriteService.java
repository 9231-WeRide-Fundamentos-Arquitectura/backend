package org.example.backendweride.platform.garage.application.commandservices;

import org.example.backendweride.platform.garage.domain.model.aggregates.Favorite;
import org.example.backendweride.platform.garage.domain.model.commands.CreateFavoriteCommand;
import org.example.backendweride.platform.garage.domain.model.commands.DeleteFavoriteCommand;
import org.example.backendweride.platform.garage.domain.model.queries.GetUserFavoritesQuery;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.FavoriteRepository;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class FavoriteService {
    private final FavoriteRepository favorites;
    private final VehicleRepository vehicles;

    public FavoriteService(FavoriteRepository favorites, VehicleRepository vehicles) {
        this.favorites = favorites;
        this.vehicles = vehicles;
    }

    public List<Favorite> handle(GetUserFavoritesQuery query) {
        return favorites.findAllByUserIdOrderByAddedAtDesc(query.userId());
    }

    @Transactional
    public Favorite handle(CreateFavoriteCommand command) {
        if (!vehicles.existsById(command.vehicleId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");
        return favorites.findByUserIdAndVehicleId(command.userId(), command.vehicleId())
                .orElseGet(() -> favorites.saveAndFlush(new Favorite(command)));
    }

    @Transactional
    public void handle(DeleteFavoriteCommand command) {
        var favorite = favorites.findById(command.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));
        if (!favorite.getUserId().equals(command.userId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Favorite belongs to another user");
        favorites.delete(favorite);
    }
}
