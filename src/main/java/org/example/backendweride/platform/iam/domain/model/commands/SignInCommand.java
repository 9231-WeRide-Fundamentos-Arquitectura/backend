package org.example.backendweride.platform.iam.domain.model.commands;

/**
 * SignInCommand
 *
 * @summary Command to sign in an existing user in WeRide Platform.
 */
public record SignInCommand(String username, String password) {}
