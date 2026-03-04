package main.backend.dto.collaborator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.backend.model.enums.CollaboratorRoleEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteCollaboratorRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Role is required")
    private CollaboratorRoleEnum role;
}
