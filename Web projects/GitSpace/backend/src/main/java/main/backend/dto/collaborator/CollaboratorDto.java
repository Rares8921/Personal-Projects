package main.backend.dto.collaborator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.backend.model.enums.CollaboratorRoleEnum;
import main.backend.model.enums.InvitationStatusEnum;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorDto {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private CollaboratorRoleEnum role;
    private InvitationStatusEnum status;
    private LocalDateTime invitedAt;
    private LocalDateTime acceptedAt;
    private String invitedByUsername;
    private String repositoryName;
}
