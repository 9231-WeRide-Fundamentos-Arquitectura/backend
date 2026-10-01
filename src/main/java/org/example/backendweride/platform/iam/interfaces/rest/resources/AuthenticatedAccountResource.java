package org.example.backendweride.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Authenticated Account Resource
 *
 * @summary Represents an authenticated account resource with its ID and token.
 */
@Schema(description = "The signed-in account ID and the JWT token to send as Bearer token.")
public record AuthenticatedAccountResource(Long id, String token) {}
