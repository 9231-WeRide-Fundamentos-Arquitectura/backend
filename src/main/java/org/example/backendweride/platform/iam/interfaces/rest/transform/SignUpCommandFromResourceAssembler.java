package org.example.backendweride.platform.iam.interfaces.rest.transform;

import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignUpResource;

/**
 * Assembler class to convert SignUpResource to SignUpCommand.
 *
 * @summary This class provides a method to transform a SignUpResource
 *          into a SignUpCommand for processing sign-up requests.
 */
public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(resource.username(), resource.password());
    }
}