package main.backend.controller;

import lombok.RequiredArgsConstructor;
import main.backend.dto.collaborator.CollaboratorDto;
import main.backend.service.CollaboratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final CollaboratorService collaboratorService;

    @GetMapping("/pending")
    public ResponseEntity<List<CollaboratorDto>> getMyPendingInvitations(Authentication authentication) {
        String currentUsername = authentication.getName();
        List<CollaboratorDto> invitations = collaboratorService.getMyPendingInvitations(currentUsername);
        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<CollaboratorDto> acceptInvitation(
            @PathVariable Long invitationId,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        CollaboratorDto collaborator = collaboratorService.acceptInvitation(invitationId, currentUsername);
        return ResponseEntity.ok(collaborator);
    }

    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @PathVariable Long invitationId,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        collaboratorService.rejectInvitation(invitationId, currentUsername);
        return ResponseEntity.noContent().build();
    }
}
