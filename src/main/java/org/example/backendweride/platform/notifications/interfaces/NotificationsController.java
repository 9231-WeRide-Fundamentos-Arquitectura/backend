package org.example.backendweride.platform.notifications.interfaces;

import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;

import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.security.core.Authentication;

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
@Tag(name = "Notifications", description = "Create, list and mark as read the notifications of the authenticated user.")
@CrossOrigin(origins = "http://localhost:4200") // <--- Aseguramos CORS aquí también
public class NotificationsController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    public NotificationsController(NotificationCommandService notificationCommandService, NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @Operation(summary = "Create a notification", description = "Create a notification for a user. The userId in the body must match the username of the authenticated account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @PostMapping
    public ResponseEntity<NotificationResource> createNotification(@RequestBody CreateNotificationResource resource, Authentication authentication) {
        CurrentUser.requireUsername(authentication, resource.userId());
        var command = CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        notificationCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Obtener notificaciones por Usuario (Extraído del Token).
     * Uso: GET /api/v1/notifications (Header Authorization: Bearer ...)
     */
    @Operation(summary = "Get my notifications", description = "List all notifications of the authenticated user; the user is taken from the JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
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

    @Operation(summary = "Get notification by ID", description = "Retrieve a single notification using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResource> getNotificationById(@Parameter(description = "Unique identifier of the notification") @PathVariable String notificationId, Authentication authentication) {
        var query = new GetNotificationByIdQuery(notificationId);
        var notification = notificationQueryService.handle(query);
        if (notification.isPresent()) CurrentUser.requireUsername(authentication, notification.get().getUserId());

        return notification.map(entity -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Mark notification as read", description = "Mark a notification of the authenticated user as read.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The resource belongs to another user")
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@Parameter(description = "Unique identifier of the notification") @PathVariable String notificationId, Authentication authentication) {
        var notification = notificationQueryService.handle(new GetNotificationByIdQuery(notificationId));
        if (notification.isEmpty()) return ResponseEntity.notFound().build();
        CurrentUser.requireUsername(authentication, notification.get().getUserId());
        var command = new MarkNotificationAsReadCommand(notificationId);
        notificationCommandService.handle(command);
        return ResponseEntity.ok("Notification marked as read");
    }
}