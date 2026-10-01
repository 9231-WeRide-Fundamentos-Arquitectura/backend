package org.example.backendweride.platform.profile.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Personal profile of a user and its preferences.")
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