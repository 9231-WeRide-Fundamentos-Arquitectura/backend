package org.example.backendweride.platform.iam.domain.model.commands;

/**
 * SignUpCommand
 *
 * @summary Command to sign up a new user in WeRide Platform.
 */

public record SignUpCommand(String username, String password) {}

