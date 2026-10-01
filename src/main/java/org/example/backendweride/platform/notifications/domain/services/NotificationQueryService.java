package org.example.backendweride.platform.notifications.domain.services;

import org.example.backendweride.platform.notifications.domain.model.aggregates.Notification;
import org.example.backendweride.platform.notifications.domain.model.queries.GetAllNotificationsByUserIdQuery;
import org.example.backendweride.platform.notifications.domain.model.queries.GetNotificationByIdQuery;

import java.util.List;
import java.util.Optional;

public interface NotificationQueryService {
    List<Notification> handle(GetAllNotificationsByUserIdQuery query);
    Optional<Notification> handle(GetNotificationByIdQuery query);
}