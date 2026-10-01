package org.example.backendweride.platform.booking.interfaces.transform;

import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingCommand;
import org.example.backendweride.platform.booking.interfaces.resources.CreateBookingResource;

public class CreateBookingCommandFromResourceAssembler {

    public static CreateBookingCommand toCommandFromResource(CreateBookingResource resource) {
        return new CreateBookingCommand(
                resource.userId(),
                resource.vehicleId(),
                resource.startLocationId(),
                resource.endLocationId()
        );
    }
}