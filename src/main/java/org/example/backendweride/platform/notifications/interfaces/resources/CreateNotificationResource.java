package org.example.backendweride.platform.notifications.interfaces.resources;

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