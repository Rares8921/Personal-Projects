package main.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data @NoArgsConstructor
@Entity
@Table(name = "follow_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "target_id"})
})
public class FollowRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_id")
    private User target;

    private Instant requestedAt = Instant.now();

    public FollowRequest(User follower, User target) {
        this.follower = follower;
        this.target = target;
    }
}