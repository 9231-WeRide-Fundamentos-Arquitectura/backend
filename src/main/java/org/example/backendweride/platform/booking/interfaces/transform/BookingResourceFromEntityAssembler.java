package org.example.backendweride.platform.booking.interfaces.transform;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.interfaces.resources.BookingResource;

public class BookingResourceFromEntityAssembler {

    /** Light resource for lists and state changes: no route. */
    public static BookingResource toResourceFromEntity(Booking entity) {
        return build(entity, null);
    }

    /** Full resource with the recorded route, for complete and history detail. */
    public static BookingResource toResourceWithRoute(Booking entity) {
        return build(entity, entity.getRouteCoordinates());
    }

    private static BookingResource build(Booking entity, java.util.List<org.example.backendweride.platform.booking.domain.model.valueobjects.TripRouteCoordinate> route) {

        BookingResource.RatingResource ratingResource = null;

        if (entity.getRating() != null) {
            ratingResource = new BookingResource.RatingResource(
                    entity.getRating().getScore(),
                    entity.getRating().getComment(), entity.getRating().tagList()
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
                ratingResource,
                route,
                entity.getRouteSource()
        );
    }
}
