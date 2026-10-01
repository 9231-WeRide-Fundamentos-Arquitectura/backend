package org.example.backendweride.platform.booking.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Rating {
    private Integer score;
    @jakarta.persistence.Column(length = 1000)
    private String comment;
    private String tags;

    public static final java.util.Set<String> ALLOWED_TAGS = java.util.Set.of("clean", "brakes_ok", "comfortable", "good_condition");
    // ponytail: Rating throws ResponseStatusException from the domain (as Booking does); move to a domain exception + controller advice if the domain must stay HTTP-free.

    public Rating(Integer score, String comment) {
        this(score, comment, java.util.List.of());
    }

    public Rating(Integer score, String comment, java.util.List<String> tags) {
        if (score == null || score < 1 || score > 5 || (comment != null && comment.length() > 1000)
                || (tags != null && (tags.size() > ALLOWED_TAGS.size() || tags.stream().anyMatch(tag -> tag == null || !ALLOWED_TAGS.contains(tag)))))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid rating");
        this.score = score;
        this.comment = comment;
        this.tags = tags == null ? "" : String.join("|", new java.util.LinkedHashSet<>(tags));
    }

    public java.util.List<String> tagList() {
        return tags == null || tags.isBlank() ? java.util.List.of() : java.util.List.of(tags.split("\\|"));
    }
}
