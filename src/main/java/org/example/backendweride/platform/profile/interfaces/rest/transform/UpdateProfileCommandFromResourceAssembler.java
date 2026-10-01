package org.example.backendweride.platform.profile.interfaces.rest.transform;

import org.example.backendweride.platform.profile.domain.model.commands.UpdateProfileCommand;
import org.example.backendweride.platform.profile.interfaces.rest.resources.UpdateUserResource;

public class UpdateProfileCommandFromResourceAssembler {
    public static UpdateProfileCommand toCommand(Long userId, UpdateUserResource resource) {
        return new UpdateProfileCommand(
                userId,
                resource.name(),
                resource.phone(),
                resource.profilePicture(),
                resource.dateOfBirth(),
                resource.address(),
                resource.emergencyContact()
        );
    }
}