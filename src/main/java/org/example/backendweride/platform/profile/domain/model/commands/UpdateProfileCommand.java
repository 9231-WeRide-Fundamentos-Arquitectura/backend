package org.example.backendweride.platform.profile.domain.model.commands;

import java.time.LocalDate;

public record UpdateProfileCommand(
        Long userId,
        String name,
        String phone,
        String profilePicture,
        LocalDate dateOfBirth,
        String address,
        String emergencyContact
) {}