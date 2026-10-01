package org.example.backendweride.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Sign In Resource
 *
 * @summary Represents the data required for signing in, including username and password.
 */
@Schema(description = "Credentials required to sign in.")
public record SignInResource(String username, String password) {}
