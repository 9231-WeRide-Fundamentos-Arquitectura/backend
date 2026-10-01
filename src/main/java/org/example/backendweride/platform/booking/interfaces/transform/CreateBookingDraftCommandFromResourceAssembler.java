package org.example.backendweride.platform.booking.interfaces.transform;

import org.example.backendweride.platform.booking.domain.model.commands.CreateBookingDraftCommand;
import org.example.backendweride.platform.booking.interfaces.resources.CreateBookingDraftResource;

public final class CreateBookingDraftCommandFromResourceAssembler {
    private CreateBookingDraftCommandFromResourceAssembler() {}
    public static CreateBookingDraftCommand toCommandFromResource(String userId, CreateBookingDraftResource resource) {
        return new CreateBookingDraftCommand(userId, resource.vehicleId(), resource.selectedDate(), resource.unlockTime(),
                resource.duration(), resource.smsReminder(), resource.emailConfirmation(), resource.pushNotification());
    }
}
