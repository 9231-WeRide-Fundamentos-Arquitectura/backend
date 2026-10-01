package org.example.backendweride.platform.iam.interfaces.rest.transform;

import org.example.backendweride.platform.iam.domain.model.commands.SignInCommand;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignInResource;

/**
 * Assembler class to convert SignInResource to SignInCommand.
 *
 * @summary This class provides a method to transform a SignInResource
 *          into a SignInCommand for processing sign-in requests.
 */
public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.username(), resource.password());
    }
}
