package org.example.backendweride.platform.booking.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "A page of completed bookings for the authenticated account, newest completed ride first. Historical bookings without recorded GPS return an empty route.")
public record BookingHistoryResource(List<BookingResource> content, long totalElements, int totalPages, int number, int size) {}
