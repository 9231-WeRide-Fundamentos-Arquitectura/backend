package org.example.backendweride.platform.iam.interfaces.rest.resources;

/**
 * Sign In Resource
 *
 * @summary Represents the data required for signing in, including username and password.
 */
public record SignInResource(String username, String password) {}
