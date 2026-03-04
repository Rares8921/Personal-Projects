package main.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = "reviews")
@Entity
@Table(name = "pull_requests")
public class PullRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private Long prNumber;
    @Column(nullable = false) private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false) private String fromBranch;
    @Column(nullable = false) private String toBranch;
    private String state = "OPEN"; // OPEN, CLOSED, MERGED

    @ManyToOne(optional = false) private Repository repository;
    @ManyToOne(optional = false) private User author;

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<PullRequestReview> reviews;
}