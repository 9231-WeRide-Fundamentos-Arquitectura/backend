package org.example.backendweride.platform.notifications.domain.model.aggregates;

import org.example.backendweride.platform.notifications.domain.model.commands.CreateNotificationCommand;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
public class Notification extends AbstractAggregateRoot<Notification> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false)
    private String userId;

    private String title;
    private String message;
    private String type;
    private String category;
    private String priority;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @SuppressWarnings("FieldMayBeFinal")
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date readAt;

    private boolean isRead;
    private boolean actionRequired;

    // Relaciones Loose Coupling
    private String relatedEntityId;
    private String relatedEntityType;

    // UI Attributes
    private String icon;
    private String color;

    protected Notification() {}

    public Notification(CreateNotificationCommand command) {
        this.publicId = UUID.randomUUID().toString();
        this.userId = command.userId();
        this.title = command.title();
        this.message = command.message();
        this.type = command.type();
        this.category = command.category();
        this.priority = command.priority();
        this.relatedEntityId = command.relatedEntityId();
        this.relatedEntityType = command.relatedEntityType();
        this.icon = command.icon();
        this.color = command.color();

        // Valores por defecto
        this.isRead = false;
        this.actionRequired = false;
        this.readAt = null;
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = new Date();
    }
}