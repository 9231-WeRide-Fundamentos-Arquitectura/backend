package org.example.backendweride.platform.notifications.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data required to create a notification for a user. userId is the username of the recipient.")
public record CreateNotificationResource(
        String userId,
        String title,
        String message,
        String type,
        String category,
        String priority,
        String relatedEntityId,
        String relatedEntityType,
        String icon,
        String color
) {}