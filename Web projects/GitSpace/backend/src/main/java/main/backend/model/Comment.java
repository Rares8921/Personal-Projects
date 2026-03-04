package main.backend.model;

import jakarta.persistence.*;
import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment { // Pt. Issue 31: Code C(omments) sau comentarii la Issue/PR
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob private String body;

    private Instant createdAt = Instant.now();

    @ManyToOne(optional = false) private User author;

    @ManyToOne private Issue issue;
    @ManyToOne private PullRequest pullRequest;

    // Pt. Issue 31:
    private String filePath;
    private Integer lineNumber;
}
