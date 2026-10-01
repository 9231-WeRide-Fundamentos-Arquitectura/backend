package org.example.backendweride.platform.profile.interfaces.rest.transform;

import org.example.backendweride.platform.profile.domain.model.aggregates.Profile;
import org.example.backendweride.platform.profile.interfaces.rest.resources.ProfileResource;

public class ProfileResourceFromAggregateAssembler {
    public static ProfileResource toResource(Profile profile) {
        return new ProfileResource(
                profile.getId(),
                profile.getUserId(),
                profile.getName(),
                profile.getPhone(),
                profile.getProfilePicture(),
                profile.getDateOfBirth(),
                profile.getAddress(),
                profile.getEmergencyContact(),
                profile.getPreferences().getLanguage(),
                profile.getPreferences().isNotifications(),
                profile.getPreferences().getTheme()
        );
    }
}