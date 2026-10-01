package org.example.backendweride.platform.notifications.interfaces.transform;

import org.example.backendweride.platform.notifications.domain.model.aggregates.Notification;
import org.example.backendweride.platform.notifications.interfaces.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getPublicId(), // Mapea al "id" público del JSON
                entity.getUserId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType(),
                entity.getCategory(),
                entity.getPriority(),
                entity.getCreatedAt(),
                entity.getReadAt(),
                entity.isRead(),
                entity.isActionRequired(),
                entity.getRelatedEntityId(),
                entity.getRelatedEntityType(),
                entity.getIcon(),
                entity.getColor()
        );
    }
}