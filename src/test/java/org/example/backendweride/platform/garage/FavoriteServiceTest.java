package org.example.backendweride.platform.garage;

import org.example.backendweride.platform.garage.application.commandservices.FavoriteService;
import org.example.backendweride.platform.garage.domain.model.aggregates.Favorite;
import org.example.backendweride.platform.garage.domain.model.commands.CreateFavoriteCommand;
import org.example.backendweride.platform.garage.domain.model.commands.DeleteFavoriteCommand;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.FavoriteRepository;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {
    @Test
    void duplicateFavoriteIsReusedAndAnotherAccountCannotDeleteIt() {
        var repository = mock(FavoriteRepository.class);
        var vehicles = mock(VehicleRepository.class);
        var service = new FavoriteService(repository, vehicles);
        var command = new CreateFavoriteCommand(42L, 7L, null);
        var favorite = new Favorite(command);
        when(vehicles.existsById(7L)).thenReturn(true);
        when(repository.findByUserIdAndVehicleId(42L, 7L)).thenReturn(Optional.of(favorite));
        assertSame(favorite, service.handle(command));
        verify(repository, never()).saveAndFlush(any());
        when(repository.findById(1L)).thenReturn(Optional.of(favorite));
        var error = assertThrows(ResponseStatusException.class, () -> service.handle(new DeleteFavoriteCommand(1L, 43L)));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(repository, never()).delete(any());
    }
}
