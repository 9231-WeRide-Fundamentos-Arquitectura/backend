package org.example.backendweride.platform.booking.interfaces.transform;

import org.example.backendweride.platform.booking.domain.model.commands.CompleteBookingCommand;
import org.example.backendweride.platform.booking.interfaces.resources.CompleteBookingResource;

public class CompleteBookingCommandFromResourceAssembler {
    public static CompleteBookingCommand toCommandFromResource(String bookingId, CompleteBookingResource resource) {
        return new CompleteBookingCommand(
                bookingId,
                resource.totalCost(),
                resource.discount(),
                resource.distance(),
                resource.duration(),
                resource.averageSpeed(),
                resource.rating().score(),
                resource.rating().comment()
        );
    }
}