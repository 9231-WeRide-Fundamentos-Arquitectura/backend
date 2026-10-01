package org.example.backendweride.platform.booking.domain.model.commands;

import java.util.List;

public record RateBookingCommand(String bookingId, String userId, Integer score, String comment, List<String> tags) {}
