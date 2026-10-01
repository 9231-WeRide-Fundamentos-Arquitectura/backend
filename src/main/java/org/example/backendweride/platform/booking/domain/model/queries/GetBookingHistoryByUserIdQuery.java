package org.example.backendweride.platform.booking.domain.model.queries;

public record GetBookingHistoryByUserIdQuery(String userId, int page, int size) {}
