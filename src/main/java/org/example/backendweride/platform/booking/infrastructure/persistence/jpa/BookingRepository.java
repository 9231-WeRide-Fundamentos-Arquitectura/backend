package org.example.backendweride.platform.booking.infrastructure.persistence.jpa;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    org.springframework.data.domain.Page<Booking> findByUserIdAndStatus(String userId, String status, org.springframework.data.domain.Pageable page);
    List<Booking> findAllByUserId(String userId);
    List<Booking> findAllByVehicleIdAndStatusIn(String vehicleId, Collection<String> statuses);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select b from Booking b where b.id = :id")
    java.util.Optional<Booking> findLockedById(@org.springframework.data.repository.query.Param("id") String id);

    @org.springframework.data.jpa.repository.Query("select avg(b.rating.score) from Booking b where b.vehicleId = :vehicleId and b.status = 'completed' and b.rating.score is not null")
    Double averageRating(@org.springframework.data.repository.query.Param("vehicleId") String vehicleId);
    @org.springframework.data.jpa.repository.Query("select b.vehicleId, avg(b.rating.score) from Booking b where b.status = 'completed' and b.rating.score is not null group by b.vehicleId")
    List<Object[]> averageRatingsByVehicle();
    List<Booking> findByUserIdAndStatusAndPaymentStatusOrderByActualEndDateDesc(String userId, String status, String paymentStatus, org.springframework.data.domain.Pageable page);
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(b.finalCost), 0) from Booking b where b.userId = :userId and b.status = 'completed' and b.paymentStatus = 'paid'")
    Double totalPaid(@org.springframework.data.repository.query.Param("userId") String userId);
}
