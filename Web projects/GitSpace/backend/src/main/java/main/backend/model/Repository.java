package main.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = {"issues", "pullRequests"}) // fara el da loop
@Entity
@Table(name = "repositories", uniqueConstraints = {
    // nu avem un user cu 2 repo-uri identice ca nume
    @UniqueConstraint(columnNames = {"owner_id", "name"})
})
public class Repository {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;
    private String description;

    private boolean isPublic;

    @Column(nullable = false)
    private String defaultBranch = "master";

    private int starsCount = 0;
    private int forksCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Issue> issues;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<PullRequest> pullRequests;

    private String license = "None";
    private boolean isArchived = false;
}
