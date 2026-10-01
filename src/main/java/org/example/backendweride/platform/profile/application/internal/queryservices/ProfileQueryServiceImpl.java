package org.example.backendweride.platform.profile.application.internal.queryservices;

import org.example.backendweride.platform.profile.domain.model.aggregates.Profile;
import org.example.backendweride.platform.profile.domain.model.queries.GetProfileByUserIdQuery;
import org.example.backendweride.platform.profile.domain.model.queries.GetProfileByIdQuery;
import org.example.backendweride.platform.profile.domain.services.ProfileQueryService;
import org.example.backendweride.platform.profile.infrastructure.persistence.jpa.ProfileEntity;
import org.example.backendweride.platform.profile.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId())
                .map(ProfileEntity::toDomain);
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId())
                .map(ProfileEntity::toDomain);
    }
}