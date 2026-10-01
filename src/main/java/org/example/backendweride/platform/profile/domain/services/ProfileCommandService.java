package org.example.backendweride.platform.profile.domain.services;

import org.example.backendweride.platform.profile.domain.model.aggregates.Profile;
import org.example.backendweride.platform.profile.domain.model.commands.CreateProfileCommand;
import org.example.backendweride.platform.profile.domain.model.commands.UpdateProfileCommand;

import java.util.Optional;

public interface ProfileCommandService {
    Profile handle(CreateProfileCommand command);
    Optional<Profile> handle(UpdateProfileCommand command);
}