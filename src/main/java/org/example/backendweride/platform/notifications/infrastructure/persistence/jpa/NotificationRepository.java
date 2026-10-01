package org.example.backendweride.platform.notifications.infrastructure.persistence.jpa;

import org.example.backendweride.platform.notifications.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByPublicId(String publicId);
    List<Notification> findAllByUserId(String userId); // Nuevo método necesario
}