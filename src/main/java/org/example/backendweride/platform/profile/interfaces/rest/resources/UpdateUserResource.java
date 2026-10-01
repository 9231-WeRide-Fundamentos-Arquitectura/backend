package org.example.backendweride.platform.profile.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Personal details that can be updated in a profile.")
public record UpdateUserResource(
        String name,
        String phone,
        String profilePicture,
        LocalDate dateOfBirth,
        String address,
        String emergencyContact
) {}