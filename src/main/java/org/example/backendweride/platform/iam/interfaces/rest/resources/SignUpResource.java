package org.example.backendweride.platform.iam.interfaces.rest.resources;

/**
 * Sign Up Resource
 *
 * @summary Represents the data required for signing up, including username and password.
 */
public record SignUpResource(String username, String password) {}
