package org.example.backendweride.platform.travelhistory.interfaces.transform;

import org.example.backendweride.platform.travelhistory.domain.model.commands.UpdateTravelHistoryCommand;
import org.example.backendweride.platform.travelhistory.interfaces.resources.UpdateTravelHistoryResource;

/**
 * Assembler to convert UpdateTravelHistoryResource to UpdateTravelHistoryCommand.
 *
 * @summary This assembler facilitates the transformation of resource objects
 *          into command objects for updating travel history records.
 */
public class UpdateTravelHistoryCommandFromResourceAssembler {
    public static UpdateTravelHistoryCommand toCommandFromResource(Long userId, UpdateTravelHistoryResource resource) {
        return new UpdateTravelHistoryCommand(
                userId,
                resource.location(),
                resource.vehicle(),
                resource.image(),
                resource.tripDuration(),
                resource.travelDistance()
        );
    }
}
