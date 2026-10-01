package org.example.backendweride.platform.notifications.interfaces.resources;

import java.util.Date;

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