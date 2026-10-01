package org.example.backendweride.platform.problemreports.interfaces;

import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.example.backendweride.platform.problemreports.application.internal.commandservices.ProblemReportCommandService;
import org.example.backendweride.platform.problemreports.domain.model.aggregates.ProblemReport;
import org.example.backendweride.platform.problemreports.domain.model.commands.CreateProblemReportCommand;
import org.example.backendweride.platform.problemreports.infrastructure.persistence.jpa.ProblemReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Problem Reports", description = "Report vehicle faults, attach an optional photo and consult personal reports. Reported vehicles enter maintenance; qualifying first-minute rides end without a charge.")
@RestController
@RequestMapping("/api/v1/problem-reports")
public class ProblemReportsController {
    private final ProblemReportCommandService service;
    private final ProblemReportRepository reports;
    public ProblemReportsController(ProblemReportCommandService service, ProblemReportRepository reports) {
        this.service = service;
        this.reports = reports;
    }
    @Schema(description = "Vehicle fault report. User and report timestamp are taken from the authenticated session and server.")
    public record CreateProblemReportResource(
            @Schema(description = "Vehicle ID", example = "1") String vehicleId,
            @Schema(description = "Required booking UUID; must be owned by the caller, match the vehicle and be reserved, in progress or completed within the last hour") String bookingId,
            @Schema(description = "At least one category: mechanical, tires, gps, battery, lock, other, brakes or damage", example = "[\"brakes\"]") List<String> categories,
            @Schema(description = "Description up to 2000 characters; may be empty", maxLength = 2000) String description,
            @Schema(description = "Optional JPEG, PNG or WebP data URL with base64 content, up to 1 MB decoded") String photo) {}
    @Operation(summary = "Create a problem report", description = "Persist a maintenance incident and mark the vehicle as maintenance so it leaves the available map. If a matching owned booking is in progress and began at most 60 seconds ago, finish it with zero charge and return chargeWaived=true. The incident is queued as pending_maintenance; no external maintenance notification is sent.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Report saved and vehicle marked for maintenance"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle ID, category, description, photo or booking/vehicle mismatch"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Booking belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Vehicle or booking not found")
    })
    @PostMapping
    public ResponseEntity<ProblemReport> create(@RequestBody CreateProblemReportResource resource, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.handle(new CreateProblemReportCommand(
                String.valueOf(CurrentUser.id(authentication)), resource.vehicleId(), resource.bookingId(),
                resource.categories(), resource.description(), resource.photo())));
    }
    @Operation(summary = "Get my problem reports", description = "Retrieve reports submitted by the authenticated user, including maintenance status and whether the charge was waived. Returns an empty list when none exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personal problem reports retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public List<ProblemReportSummary> getMine(Authentication authentication) {
        return reports.findAllByUserId(String.valueOf(CurrentUser.id(authentication))).stream()
                .map(r -> new ProblemReportSummary(r.getId(), r.getUserId(), r.getVehicleId(), r.getBookingId(), r.getCategories(),
                        r.getDescription(), r.getPhoto() != null && !r.getPhoto().isBlank(), r.getStatus(), r.getReportDate(), r.isChargeWaived()))
                .toList();
    }
    @Schema(description = "Problem report as listed: same as the detail but without the base64 photo; fetch GET /{id} for it.")
    public record ProblemReportSummary(String id, String userId, String vehicleId, String bookingId, List<String> categories,
            String description, boolean hasPhoto, String status, java.util.Date reportDate, boolean chargeWaived) {}
    @Operation(summary = "Get a problem report by ID", description = "Retrieve a single maintenance report owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem report retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Report belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Problem report not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProblemReport> getById(@Parameter(description = "Problem report UUID") @PathVariable String id, Authentication authentication) {
        return reports.findById(id).map(report -> {
            CurrentUser.requireSelf(authentication, report.getUserId());
            return ResponseEntity.ok(report);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
