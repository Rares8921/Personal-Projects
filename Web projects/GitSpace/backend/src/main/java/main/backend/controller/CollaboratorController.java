package main.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.backend.dto.collaborator.CollaboratorDto;
import main.backend.dto.collaborator.InviteCollaboratorRequest;
import main.backend.service.CollaboratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories/{repositoryId}/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    @GetMapping
    public ResponseEntity<List<CollaboratorDto>> getCollaborators(@PathVariable Long repositoryId) {
        List<CollaboratorDto> collaborators = collaboratorService.getCollaborators(repositoryId);
        return ResponseEntity.ok(collaborators);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CollaboratorDto>> getActiveCollaborators(@PathVariable Long repositoryId) {
        List<CollaboratorDto> collaborators = collaboratorService.getActiveCollaborators(repositoryId);
        return ResponseEntity.ok(collaborators);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CollaboratorDto>> getPendingInvitations(@PathVariable Long repositoryId) {
        List<CollaboratorDto> invitations = collaboratorService.getPendingInvitations(repositoryId);
        return ResponseEntity.ok(invitations);
    }

    @PostMapping
    public ResponseEntity<CollaboratorDto> inviteCollaborator(
            @PathVariable Long repositoryId,
            @Valid @RequestBody InviteCollaboratorRequest request,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        CollaboratorDto collaborator = collaboratorService.inviteCollaborator(repositoryId, request, currentUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborator);
    }

    @DeleteMapping("/{collaboratorId}")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable Long repositoryId,
            @PathVariable Long collaboratorId,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        collaboratorService.removeCollaborator(repositoryId, collaboratorId, currentUsername);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-access")
    public ResponseEntity<Boolean> checkWriteAccess(
            @PathVariable Long repositoryId,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        boolean hasAccess = collaboratorService.hasWriteAccess(repositoryId, currentUsername);
        return ResponseEntity.ok(hasAccess);
    }
}