package org.example.backendweride.platform.profile.application.internal.commandservices;

import org.example.backendweride.platform.profile.domain.model.aggregates.Profile;
import org.example.backendweride.platform.profile.domain.model.commands.CreateProfileCommand;
import org.example.backendweride.platform.profile.domain.model.commands.UpdateProfileCommand;
import org.example.backendweride.platform.profile.domain.services.ProfileCommandService;
import org.example.backendweride.platform.profile.infrastructure.persistence.jpa.ProfileEntity;
import org.example.backendweride.platform.profile.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile handle(CreateProfileCommand command) {
        if (profileRepository.existsByUserId(command.userId())) {
            throw new RuntimeException("Un perfil para esta cuenta ya existe.");
        }
        var profile = new Profile(command);
        var entity = new ProfileEntity(profile);
        var savedEntity = profileRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        var entity = profileRepository.findById(command.userId());
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        var profile = entity.get().toDomain();
        profile.update(command);
        var updatedEntity = profileRepository.save(new ProfileEntity(profile));
        return Optional.of(updatedEntity.toDomain());
    }
}