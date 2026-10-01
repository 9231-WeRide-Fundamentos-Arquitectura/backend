package org.example.backendweride.platform.booking.domain.model.commands;

public record CreateBookingDraftCommand(String userId, String vehicleId, String selectedDate, String unlockTime,
                                       double duration, boolean smsReminder, boolean emailConfirmation, boolean pushNotification) {}
