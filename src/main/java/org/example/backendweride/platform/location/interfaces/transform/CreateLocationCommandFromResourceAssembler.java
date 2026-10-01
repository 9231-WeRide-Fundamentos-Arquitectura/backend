package org.example.backendweride.platform.location.interfaces.transform;

import org.example.backendweride.platform.location.domain.commands.CreateLocationCommand;
import org.example.backendweride.platform.location.interfaces.resources.CreateLocationResource;

public class CreateLocationCommandFromResourceAssembler {
    public static CreateLocationCommand toCommandFromResource(CreateLocationResource resource) {
        return new CreateLocationCommand(
                resource.name(),
                resource.address(),
                resource.coordinates(),
                resource.type(),
                resource.capacity(),
                resource.availableSpots(),
                resource.isActive(),
                resource.operatingHours(),
                resource.amenities(),
                resource.district(),
                resource.description(),
                resource.image()
        );
    }
}
