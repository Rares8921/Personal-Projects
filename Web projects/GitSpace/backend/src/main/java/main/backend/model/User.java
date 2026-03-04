package main.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "repositories", "following" }) // fara el da loop
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    public String passwordHash;

    @Column(nullable = false, columnDefinition = "boolean default true")
    public Boolean isPublic = true;

    private Instant createdAt = Instant.now();

    private String fullName; // Pt. Settings (Issue 4)
    private String bio; // Pt. Settings (Issue 4)
    private String avatarUrl; // Pt. Issue 18
    private String timezone; // Pt. Issue 10

    private String location;
    private String website;

    // Pt Issue 17 urm 2
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpiry;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private Set<Repository> repositories;

    @OneToMany(mappedBy = "follower") // Oribil, dar pt. Issue 2
    @JsonIgnore
    private Set<UserFollow> following;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;
}
