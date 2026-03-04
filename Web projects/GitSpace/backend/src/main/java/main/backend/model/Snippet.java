package main.backend.model;

import jakarta.persistence.*;
import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "snippets")
public class Snippet { // Pt. Issue 8: Code Snippet
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String filename;
    @Lob private String content;
    private boolean isPublic;

    private String language;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(optional = false) private User owner;
}
