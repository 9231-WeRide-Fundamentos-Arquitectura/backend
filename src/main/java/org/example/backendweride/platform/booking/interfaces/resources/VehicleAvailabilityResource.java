package org.example.backendweride.platform.booking.interfaces.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Whether a vehicle is free in a time range, plus its busy slots. Slots are anonymous: they never expose who booked.")
public record VehicleAvailabilityResource(boolean available, List<BusySlot> busySlots) {
    public record BusySlot(Date startDate, Date endDate) {}
}
