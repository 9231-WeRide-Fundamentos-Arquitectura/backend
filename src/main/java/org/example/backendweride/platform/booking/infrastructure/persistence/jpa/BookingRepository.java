package org.example.backendweride.platform.booking.infrastructure.persistence.jpa;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findAllByUserId(String userId);
}