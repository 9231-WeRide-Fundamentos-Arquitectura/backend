package org.example.backendweride.platform.booking.interfaces.transform;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.interfaces.resources.BookingResource;

public class BookingResourceFromEntityAssembler {

    public static BookingResource toResourceFromEntity(Booking entity) {

        BookingResource.RatingResource ratingResource = null;

        if (entity.getRating() != null) {
            ratingResource = new BookingResource.RatingResource(
                    entity.getRating().getScore(),
                    entity.getRating().getComment()
            );
        }

        return new BookingResource(
                entity.getId(),
                entity.getUserId(),
                entity.getVehicleId(),
                entity.getStartLocationId(),
                entity.getEndLocationId(),
                entity.getReservedAt(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getActualStartDate(),
                entity.getActualEndDate(),
                entity.getStatus(),
                entity.getTotalCost(),
                entity.getDiscount(),
                entity.getFinalCost(),
                entity.getPaymentMethod(),
                entity.getPaymentStatus(),
                entity.getDistance(),
                entity.getDuration(),
                entity.getAverageSpeed(),
                ratingResource
        );
    }
}