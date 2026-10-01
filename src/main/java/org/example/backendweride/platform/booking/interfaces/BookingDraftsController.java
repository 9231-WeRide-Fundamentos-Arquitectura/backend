package org.example.backendweride.platform.booking.interfaces;

import jakarta.validation.Valid;
import org.example.backendweride.platform.booking.domain.model.aggregates.BookingDraft;
import org.example.backendweride.platform.booking.application.commandservices.BookingDraftService;
import org.example.backendweride.platform.booking.interfaces.transform.CreateBookingDraftCommandFromResourceAssembler;
import org.example.backendweride.platform.booking.interfaces.resources.CreateBookingDraftResource;
import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/bookingDrafts")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Booking Drafts", description = "Save and remove personal booking drafts. Drafts expire 24 hours after saving and do not reserve or unlock a vehicle. Lists use zero-based page numbers and a maximum page size of 100.")
public class BookingDraftsController {
    private final BookingDraftService service;
    public BookingDraftsController(BookingDraftService service) { this.service = service; }

    @Operation(summary = "Get my unexpired booking drafts", description = "List only the authenticated user's drafts whose server expiry is in the future, ordered by most recently saved. Pagination uses a zero-based page and a page size; returns an empty list when there are no unexpired drafts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unexpired personal drafts retrieved"),
            @ApiResponse(responseCode = "400", description = "Page must be non-negative and limit must be between 1 and 100"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping
    public List<BookingDraft> list(Authentication authentication,
            @Parameter(description = "Zero-based page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of drafts per page, from 1 to 100", example = "100") @RequestParam(defaultValue = "100") int limit) {
        return service.list(String.valueOf(CurrentUser.id(authentication)), page, limit);
    }

    @Operation(summary = "Save a booking draft", description = "Create a draft for an existing vehicle with a valid calendar date, local time and duration of 0.25 to 24 hours. The server supplies the owner, UUID, savedAt and expiresAt (24 hours after saving). A draft does not check availability, create a reservation or send reminder messages; reminder fields preserve the selected preferences.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Draft saved; Location header contains the new draft URL"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid vehicle ID, date, time or duration"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId in the body refers to another account"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PostMapping
    public ResponseEntity<BookingDraft> create(@Valid @RequestBody CreateBookingDraftResource resource, Authentication authentication) {
        var userId = String.valueOf(CurrentUser.id(authentication));
        if (resource.userId() != null) CurrentUser.requireSelf(authentication, resource.userId());
        var draft = service.handle(CreateBookingDraftCommandFromResourceAssembler.toCommandFromResource(userId, resource));
        return ResponseEntity.created(URI.create("/api/v1/bookingDrafts/" + draft.getId())).body(draft);
    }

    @Operation(summary = "Delete a booking draft", description = "Permanently remove a draft owned by the authenticated user. Expired drafts can also be deleted; deleting a draft does not affect reservations.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Draft deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Draft belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Draft not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Booking draft UUID") @PathVariable String id, Authentication authentication) {
        service.delete(String.valueOf(CurrentUser.id(authentication)), id);
        return ResponseEntity.noContent().build();
    }
}
