package main.backend.model;

import jakarta.persistence.*;
import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "activities")
public class Activity { // Pt. Issue 6: Basic Activity
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String description; // ex: "user X created repo Y"
    @Column(nullable = false) private Instant timestamp = Instant.now();

    @ManyToOne(optional = false) private User user;
}