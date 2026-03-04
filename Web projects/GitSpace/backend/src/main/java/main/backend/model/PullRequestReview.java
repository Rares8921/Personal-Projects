package main.backend.model;

import jakarta.persistence.*;
import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "pr_reviews")
public class PullRequestReview { // Pt. Issue 34: PR Approval
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private PullRequest pullRequest;
    @ManyToOne(optional = false) private User reviewer;
    private String state; // APPROVED, CHANGES_REQUESTED
    private Instant submittedAt = Instant.now();
}
