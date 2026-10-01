package org.example.backendweride.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Account Resource
 *
 * @summary Represents an account resource with its ID and username.
 */
@Schema(description = "A user account with its ID and username.")
public record AccountResource(Long id, String username) {}
