package org.example.backendweride.platform.notifications.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "A notification sent to a user, with its read status and related entity.")
public record NotificationResource(
        String id,
        String userId,
        String title,
        String message,
        String type,
        String category,
        String priority,
        Date createdAt,
        Date readAt,
        boolean isRead,
        boolean actionRequired,
        String relatedEntityId,
        String relatedEntityType,
        String icon,
        String color
) {}