package org.example.backendweride.platform.profile.interfaces.rest.resources;

import java.time.LocalDate;

public record UpdateUserResource(
        String name,
        String phone,
        String profilePicture,
        LocalDate dateOfBirth,
        String address,
        String emergencyContact
) {}