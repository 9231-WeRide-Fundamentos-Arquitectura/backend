package org.example.backendweride.platform.problemreports.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Personal vehicle incident queued for maintenance, including the optional photo and first-minute charge-waiver result.")
@Entity
@Getter
@NoArgsConstructor
@Table(name = "problem_reports")
public class ProblemReport {
    @Schema(description = "Server-generated report UUID")
    @Id private String id;
    @Schema(description = "Reporting account ID", example = "1")
    private String userId;
    @Schema(description = "Reported vehicle ID", example = "7")
    private String vehicleId;
    @Schema(description = "Related owned booking UUID; null for a report without a booking", nullable = true)
    private String bookingId;
    @Schema(description = "Selected incident categories: mechanical, tires, gps, battery, lock, other, brakes or damage", example = "[\"brakes\"]")
    @ElementCollection(fetch = FetchType.EAGER) private List<String> categories;
    @Schema(description = "User-provided description with surrounding whitespace removed", maxLength = 2000)
    @Column(length = 2000) private String description;
    @Schema(description = "Optional JPEG, PNG or WebP base64 data URL, with at most 1 MB decoded content", nullable = true)
    @Column(columnDefinition = "LONGTEXT") private String photo;
    @Schema(description = "Maintenance queue status; no external maintenance service is notified", allowableValues = {"pending_maintenance"})
    private String status;
    @Schema(description = "Server timestamp at which the report was saved")
    private Date reportDate;
    @Schema(description = "True if the related in-progress ride began at most 60 seconds before reporting and was completed with zero charge")
    private boolean chargeWaived;

    public ProblemReport(String userId, String vehicleId, String bookingId, List<String> categories,
                         String description, String photo, boolean chargeWaived) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.bookingId = bookingId;
        this.categories = List.copyOf(categories);
        this.description = description;
        this.photo = photo;
        this.chargeWaived = chargeWaived;
        this.status = "pending_maintenance";
        this.reportDate = new Date();
    }
}
