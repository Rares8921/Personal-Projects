package main.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = "comments") // fara el da loop
@Entity
@Table(name = "issues")
public class Issue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private Long issueNumber;
    @Column(nullable = false) private String title;
    @Lob private String body;
    private boolean isOpen = true;
    private Instant createdAt = Instant.now();

    @ManyToOne(optional = false) @ToString.Exclude private Repository repository;
    @ManyToOne(optional = false) @ToString.Exclude private User author;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Comment> comments;
}
