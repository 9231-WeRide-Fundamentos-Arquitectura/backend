package org.example.backendweride.platform.notifications.domain.services;

import org.example.backendweride.platform.notifications.domain.model.commands.CreateNotificationCommand;
import org.example.backendweride.platform.notifications.domain.model.commands.MarkNotificationAsReadCommand;

public interface NotificationCommandService {
    Long handle(CreateNotificationCommand command);
    void handle(MarkNotificationAsReadCommand command); // Nuevo método
}