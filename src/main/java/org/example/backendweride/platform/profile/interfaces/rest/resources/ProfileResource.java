package org.example.backendweride.platform.profile.interfaces.rest.resources;

import java.time.LocalDate;

public record ProfileResource(
        Long id,
        Long accountId,
        String name,
        String phone,
        String profilePicture,
        LocalDate dateOfBirth,
        String address,
        String emergencyContact,
        String language,
        boolean notifications,
        String theme
) {}