package org.example.backendweride.platform.unlock.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Code submitted for a simulated unlock attempt. Status, attempt count and timestamps are controlled by the server.")
public record AttemptUnlockCommand(
        @Schema(description = "Method used to interpret the submitted code", allowableValues = {"manual", "qr_code"}, example = "qr_code") String method,
        @Schema(description = "QR content weride:vehicle:<id>, or the real vehicle license plate for manual unlock. Manual comparison ignores case and surrounding spaces.", maxLength = 100, example = "weride:vehicle:7") String unlockCode) {}
