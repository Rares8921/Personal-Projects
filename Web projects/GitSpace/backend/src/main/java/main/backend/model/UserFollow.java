package main.backend.model;

import jakarta.persistence.*;
import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "user_follows")
public class UserFollow { // Pt. Issue 2: User Follow
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private User follower; // care urmareste
    @ManyToOne(optional = false) private User following; // urmarit
}