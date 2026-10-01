package org.example.backendweride.platform.iam.interfaces.rest.resources;

/**
 * Authenticated Account Resource
 *
 * @summary Represents an authenticated account resource with its ID and token.
 */
public record AuthenticatedAccountResource(Long id, String token) {}
