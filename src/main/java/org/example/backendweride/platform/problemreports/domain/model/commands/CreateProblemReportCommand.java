package org.example.backendweride.platform.problemreports.domain.model.commands;

import java.util.List;
public record CreateProblemReportCommand(String userId, String vehicleId, String bookingId,
                                        List<String> categories, String description, String photo) {}
