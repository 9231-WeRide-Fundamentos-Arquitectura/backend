package org.example.backendweride.platform.garage.interfaces.rest.transform;

import org.example.backendweride.platform.garage.domain.model.aggregates.Favorite;
import org.example.backendweride.platform.garage.interfaces.rest.resources.FavoriteResource;

public class FavoriteResourceFromEntityAssembler {
    public static FavoriteResource toResourceFromEntity(Favorite favorite) {
        return new FavoriteResource(favorite.getId().toString(), favorite.getUserId().toString(),
                favorite.getVehicleId().toString(), favorite.getAddedAt(), favorite.getNotes());
    }
}
