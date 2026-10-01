package org.example.backendweride.platform.unlock.interfaces;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.example.backendweride.platform.unlock.application.UnlockRequestService;
import org.example.backendweride.platform.unlock.domain.model.AttemptUnlockCommand;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Unlock Requests", description = "Request a simulated vehicle unlock, inspect its state and retry a QR or manual code. Requests belong to the authenticated user.")
@RestController
@RequestMapping("/api/v1/unlockRequests")
public class UnlockRequestsController {
    private final UnlockRequestService service;
    public UnlockRequestsController(UnlockRequestService service) { this.service = service; }
    @Schema(name = "CreateUnlockRequestResource", description = "Create a pending unlock request for a reserved or in-progress booking owned by the authenticated user. The server takes the scheduled time from the booking.")
    public record CreateResource(
            @Schema(description = "Booking UUID") @NotBlank String bookingId,
            @Schema(description = "Vehicle ID matching the booking", example = "1") @NotBlank String vehicleId,
            @Schema(description = "Unlock method", allowableValues = {"manual", "qr_code"}) @NotBlank String method) {}

    @Operation(summary = "Create an unlock request", description = "Create a pending request for a booking of the authenticated user. Creating the request does not start the ride. Request timestamps and scheduled time are set by the server.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pending unlock request created"),
            @ApiResponse(responseCode = "400", description = "Invalid method, missing fields or vehicle does not match booking"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Booking belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "409", description = "Booking already completed or cancelled")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnlockRequestResource create(@Valid @RequestBody CreateResource resource, Authentication authentication) {
        return UnlockRequestResource.from(service.create(String.valueOf(CurrentUser.id(authentication)),
                resource.bookingId(), resource.vehicleId(), resource.method()));
    }

    @Operation(summary = "Get my unlock requests", description = "List the authenticated user's requests in newest-first order, optionally filtered by booking. Returns an empty list when none exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unlock requests retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId filter refers to another account")
    })
    @GetMapping
    public List<UnlockRequestResource> list(@Parameter(description = "Optional booking UUID") @RequestParam(required = false) String bookingId,
            @Parameter(description = "Optional account ID; must match the authenticated user") @RequestParam(required = false) String userId, Authentication authentication) {
        if (userId != null) CurrentUser.requireSelf(authentication, userId);
        return service.list(String.valueOf(CurrentUser.id(authentication)), bookingId).stream()
                .map(UnlockRequestResource::from).toList();
    }

    @Operation(summary = "Get an unlock request by ID", description = "Retrieve the pending, unlocked or failed state of a request owned by the authenticated user, including attempts and server timestamps.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unlock request retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Request belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Unlock request not found")
    })
    @GetMapping("/{id}")
    public UnlockRequestResource get(@Parameter(description = "Unlock request UUID") @PathVariable String id, Authentication authentication) {
        return UnlockRequestResource.from(service.owned(id, String.valueOf(CurrentUser.id(authentication))));
    }

    @Operation(summary = "Attempt or retry a vehicle unlock", description = "Validate QR content weride:vehicle:<id> or the vehicle license plate for manual unlock. A recognized code changes the request to unlocked and starts the reserved booking transactionally; an unrecognized code returns 200 with status failed and permits retry. The server sets actualUnlockTime. Future or expired bookings cannot unlock; this simulation has no camera, IoT command or automatic timed unlock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attempt processed; inspect status unlocked or failed and errorMessage"),
            @ApiResponse(responseCode = "400", description = "Invalid method or missing/oversized code"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Request or booking belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Request, booking or vehicle not found"),
            @ApiResponse(responseCode = "409", description = "Booking is future, expired, completed or cancelled, or vehicle is in maintenance")
    })
    @PatchMapping("/{id}")
    public UnlockRequestResource attempt(@Parameter(description = "Unlock request UUID") @PathVariable String id, @RequestBody AttemptUnlockCommand command,
            Authentication authentication) {
        return UnlockRequestResource.from(service.attempt(id, String.valueOf(CurrentUser.id(authentication)), command));
    }
}
