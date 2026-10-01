package org.example.backendweride.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Sign Up Resource
 *
 * @summary Represents the data required for signing up, including username and password.
 */
@Schema(description = "Data required to register a new account.")
public record SignUpResource(String username, String password) {}
