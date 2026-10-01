package org.example.backendweride.platform.booking.interfaces;

import org.example.backendweride.platform.booking.domain.model.aggregates.Booking;
import org.example.backendweride.platform.booking.infrastructure.persistence.jpa.BookingRepository;
import org.example.backendweride.platform.iam.infrastructure.auth.model.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/payments")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Payments", description = "Simulated paid bookings of the authenticated user, with no stored card data. GET pagination defaults to page 0, limit 100 (max 100).")
public class PaymentsController {
    private final BookingRepository bookings;
    public PaymentsController(BookingRepository bookings) { this.bookings = bookings; }

    @Schema(description = "Simulated payment derived from a completed, paid booking. No real charge, card number or CVV is stored.")
    public record PaymentResource(
            @Schema(description = "Payment ID, equal to the booking UUID") String id,
            @Schema(description = "Authenticated account ID", example = "1") String userId,
            @Schema(description = "Completed booking UUID") String bookingId,
            @Schema(description = "Final booking cost in the indicated currency", example = "12.5") Double amount,
            @Schema(description = "Currency code", allowableValues = {"USD"}) String currency,
            @Schema(description = "Booking payment method; card is exposed as credit_card", example = "credit_card") String method,
            @Schema(description = "Simulated payment state", allowableValues = {"completed"}) String status,
            @Schema(description = "Local simulated reference: booking- followed by the booking UUID") String transactionId,
            @Schema(description = "Server timestamp at which the booking ended") Date processedAt,
            @Schema(description = "Booking description containing the vehicle ID", example = "WeRide · 1") String description) {}

    private PaymentResource resource(Booking booking) {
        return new PaymentResource(booking.getId(), booking.getUserId(), booking.getId(), booking.getFinalCost(), "USD",
                "card".equals(booking.getPaymentMethod()) ? "credit_card" : booking.getPaymentMethod(), "completed",
                "booking-" + booking.getId(), booking.getActualEndDate(), "WeRide · " + booking.getVehicleId());
    }

    @Operation(summary = "Get my simulated payments", description = "List completed, paid bookings of the authenticated account as simulated payments, ordered by the latest ride completion. Pagination uses a zero-based page and a page size. Returns an empty list when no paid bookings exist; this endpoint does not process payments or expose card details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulated payments retrieved"),
            @ApiResponse(responseCode = "400", description = "Page must be non-negative and limit must be between 1 and 100"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId filter refers to another account")
    })
    @GetMapping
    public List<PaymentResource> list(Authentication authentication,
            @Parameter(description = "Optional account ID; must match the authenticated user") @RequestParam(required = false) String userId,
            @Parameter(description = "Zero-based page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of payments per page, from 1 to 100", example = "100") @RequestParam(defaultValue = "100") int limit) {
        if (userId != null) CurrentUser.requireSelf(authentication, userId);
        if (page < 0 || limit < 1 || limit > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page");
        return bookings.findByUserIdAndStatusAndPaymentStatusOrderByActualEndDateDesc(String.valueOf(CurrentUser.id(authentication)),
                "completed", "paid", org.springframework.data.domain.PageRequest.of(page, limit)).stream().map(this::resource).toList();
    }

    @Operation(summary = "Get a simulated payment by ID", description = "Retrieve the payment derived from an owned booking. The payment ID is the booking UUID; only bookings with completed status and paid payment status are returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulated payment retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Booking belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Booking not found or it is not completed and paid")
    })
    @GetMapping("/{id}")
    public PaymentResource get(@Parameter(description = "Payment ID, equal to the booking UUID") @PathVariable String id, Authentication authentication) {
        var booking = bookings.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        CurrentUser.requireSelf(authentication, booking.getUserId());
        if (!"completed".equals(booking.getStatus()) || !"paid".equals(booking.getPaymentStatus()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return resource(booking);
    }

    @Schema(description = "Total spent on all completed, paid bookings of the authenticated user, independent of payment-list pagination.")
    public record PaymentSummary(
            @Schema(description = "Sum of final costs; zero when no paid bookings exist", example = "25.0") Double totalSpent,
            @Schema(description = "Currency code", allowableValues = {"USD"}) String currency) {}

    @Operation(summary = "Get my payment total", description = "Sum final costs of all completed, paid bookings of the authenticated account. This is the full simulated spending total rather than the sum of the current payment-list page.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment total retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "The userId filter refers to another account")
    })
    @GetMapping("/summary")
    public PaymentSummary summary(Authentication authentication,
            @Parameter(description = "Optional account ID; must match the authenticated user") @RequestParam(required = false) String userId) {
        if (userId != null) CurrentUser.requireSelf(authentication, userId);
        return new PaymentSummary(bookings.totalPaid(String.valueOf(CurrentUser.id(authentication))), "USD");
    }
}
