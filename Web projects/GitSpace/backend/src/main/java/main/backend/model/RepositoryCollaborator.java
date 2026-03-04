package main.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import main.backend.model.enums.CollaboratorRoleEnum;
import main.backend.model.enums.InvitationStatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repository_collaborators", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "repository_id", "user_id" })
})
public class RepositoryCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaboratorRoleEnum role = CollaboratorRoleEnum.WRITE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatusEnum status = InvitationStatusEnum.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime invitedAt;

    private LocalDateTime acceptedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id")
    private User invitedBy;
}
