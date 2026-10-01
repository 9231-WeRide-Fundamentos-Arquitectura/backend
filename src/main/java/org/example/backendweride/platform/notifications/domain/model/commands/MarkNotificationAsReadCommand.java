package org.example.backendweride.platform.notifications.domain.model.commands;

/**
 * Comando para marcar una notificación como leída.
 * Solo necesitamos el ID público de la notificación.
 */
public record MarkNotificationAsReadCommand(String notificationId) {
}