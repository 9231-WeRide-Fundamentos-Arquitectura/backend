package org.example.backendweride.platform.notifications.interfaces;

import org.example.backendweride.platform.notifications.domain.model.queries.GetAllNotificationsByUserIdQuery;
import org.example.backendweride.platform.notifications.domain.model.queries.GetNotificationByIdQuery;
import org.example.backendweride.platform.notifications.domain.services.NotificationCommandService;
import org.example.backendweride.platform.notifications.domain.services.NotificationQueryService;
import org.example.backendweride.platform.notifications.interfaces.resources.CreateNotificationResource;
import org.example.backendweride.platform.notifications.interfaces.resources.NotificationResource;
import org.example.backendweride.platform.notifications.interfaces.transform.CreateNotificationCommandFromResourceAssembler;
import org.example.backendweride.platform.notifications.interfaces.transform.NotificationResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.backendweride.platform.notifications.domain.model.commands.MarkNotificationAsReadCommand;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notification Management Endpoints")
@CrossOrigin(origins = "http://localhost:4200") // <--- Aseguramos CORS aquí también
public class NotificationsController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    public NotificationsController(NotificationCommandService notificationCommandService, NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping
    public ResponseEntity<NotificationResource> createNotification(@RequestBody CreateNotificationResource resource) {
        var command = CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        notificationCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Obtener notificaciones por Usuario (Extraído del Token).
     * Uso: GET /api/v1/notifications (Header Authorization: Bearer ...)
     */
    @GetMapping
    public ResponseEntity<List<NotificationResource>> getAllNotificationsByUserId(Authentication authentication) {

        // 1. Validación de Seguridad
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Extraemos el ID del token (¡Adiós @RequestParam!)
        String userId = authentication.getName();
        System.out.println("Buscando notificaciones para el usuario (Token): " + userId);

        // 3. Ejecutamos la lógica normal usando ese ID seguro
        var query = new GetAllNotificationsByUserIdQuery(userId);
        var notifications = notificationQueryService.handle(query);

        var resources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable String notificationId) {
        var query = new GetNotificationByIdQuery(notificationId);
        var notification = notificationQueryService.handle(query);

        return notification.map(entity -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable String notificationId) {
        var command = new MarkNotificationAsReadCommand(notificationId);
        notificationCommandService.handle(command);
        return ResponseEntity.ok("Notification marked as read");
    }
}