package org.example.backendweride.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * ProfileId Value Object
 *
 * @summary Represents a unique identifier for a user profile.
 */
@Embeddable
public record ProfileId(Long profileId) {
    public ProfileId {
        if (profileId == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
    }
}
