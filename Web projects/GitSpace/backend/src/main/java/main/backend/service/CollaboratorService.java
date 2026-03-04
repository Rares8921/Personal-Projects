package main.backend.service;

import lombok.RequiredArgsConstructor;
import main.backend.dto.collaborator.CollaboratorDto;
import main.backend.dto.collaborator.InviteCollaboratorRequest;
import main.backend.exception.BadRequestException;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.Repository;
import main.backend.model.RepositoryCollaborator;
import main.backend.model.User;
import main.backend.model.enums.InvitationStatusEnum;
import main.backend.repository.RepositoryCollaboratorRepository;
import main.backend.repository.RepositoryRepository;
import main.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final RepositoryCollaboratorRepository collaboratorRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;

    private static final String RESOURCE_NOT_FOUND_REPO = "Repository not found";
    private static final String RESOURCE_NOT_FOUND_USER = "User not found";

    @Transactional(readOnly = true)
    public List<CollaboratorDto> getCollaborators(Long repositoryId) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_REPO));

        return collaboratorRepository.findByRepository(repository).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CollaboratorDto> getActiveCollaborators(Long repositoryId) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_REPO));

        return collaboratorRepository
                .findByRepositoryAndStatus(repository, InvitationStatusEnum.ACCEPTED)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CollaboratorDto> getPendingInvitations(Long repositoryId) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_REPO));

        return collaboratorRepository
                .findByRepositoryAndStatus(repository, InvitationStatusEnum.PENDING)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public CollaboratorDto inviteCollaborator(Long repositoryId, InviteCollaboratorRequest request,
            String currentUsername) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_REPO));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        if (!repository.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only repository owner can invite collaborators");
        }

        User userToInvite = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Couldn't find this user"));

        if (userToInvite.getId().equals(currentUser.getId())) {
            throw new BadRequestException("Cannot invite yourself as collaborator");
        }

        // Check for existing collaboration record
        Optional<RepositoryCollaborator> existingOpt = collaboratorRepository.findByRepositoryAndUser(repository,
                userToInvite);

        if (existingOpt.isPresent()) {
            RepositoryCollaborator existing = existingOpt.get();

            if (existing.getStatus() == InvitationStatusEnum.PENDING) {
                throw new BadRequestException("User already has a pending invitation");
            }

            if (existing.getStatus() == InvitationStatusEnum.ACCEPTED) {
                throw new BadRequestException("User is already a collaborator");
            }

            // If REJECTED or REVOKED, update the existing record to re-invite
            if (existing.getStatus() == InvitationStatusEnum.REJECTED ||
                    existing.getStatus() == InvitationStatusEnum.REVOKED) {
                existing.setStatus(InvitationStatusEnum.PENDING);
                existing.setRole(request.getRole());
                existing.setInvitedBy(currentUser);
                existing.setInvitedAt(LocalDateTime.now());
                existing.setAcceptedAt(null); // Reset acceptance date

                existing = collaboratorRepository.save(existing);
                return mapToDto(existing);
            }
        }

        // Create new invitation
        RepositoryCollaborator collaborator = new RepositoryCollaborator();
        collaborator.setRepository(repository);
        collaborator.setUser(userToInvite);
        collaborator.setRole(request.getRole());
        collaborator.setStatus(InvitationStatusEnum.PENDING);
        collaborator.setInvitedBy(currentUser);

        collaborator = collaboratorRepository.save(collaborator);

        return mapToDto(collaborator);
    }

    @Transactional
    public CollaboratorDto acceptInvitation(Long collaboratorId, String username) {
        RepositoryCollaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_USER));

        if (!collaborator.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("This invitation is not for you");
        }

        if (collaborator.getStatus() != InvitationStatusEnum.PENDING) {
            throw new BadRequestException("Invitation is not pending");
        }

        collaborator.setStatus(InvitationStatusEnum.ACCEPTED);
        collaborator.setAcceptedAt(LocalDateTime.now());

        collaborator = collaboratorRepository.save(collaborator);

        return mapToDto(collaborator);
    }

    @Transactional
    public void rejectInvitation(Long collaboratorId, String username) {
        RepositoryCollaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_USER));

        if (!collaborator.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("This invitation is not for you");
        }

        if (collaborator.getStatus() != InvitationStatusEnum.PENDING) {
            throw new BadRequestException("Invitation is not pending");
        }

        collaborator.setStatus(InvitationStatusEnum.REJECTED);
        collaboratorRepository.save(collaborator);
    }

    @Transactional
    public void removeCollaborator(Long repositoryId, Long collaboratorId, String currentUsername) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_REPO));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        if (!repository.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only repository owner can remove collaborators");
        }

        RepositoryCollaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));

        if (!collaborator.getRepository().getId().equals(repositoryId)) {
            throw new BadRequestException("Collaborator does not belong to this repository");
        }

        collaborator.setStatus(InvitationStatusEnum.REVOKED);
        collaboratorRepository.save(collaborator);
    }

    @Transactional(readOnly = true)
    public boolean hasWriteAccess(Long repositoryId, String username) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_REPO));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_USER));

        if (repository.getOwner().getId().equals(user.getId())) {
            return true;
        }

        return collaboratorRepository.hasWriteAccess(repository, user);
    }

    @Transactional(readOnly = true)
    public List<CollaboratorDto> getMyPendingInvitations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_USER));

        return collaboratorRepository.findByUserAndStatus(user, InvitationStatusEnum.PENDING)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private CollaboratorDto mapToDto(RepositoryCollaborator collaborator) {
        CollaboratorDto dto = new CollaboratorDto();
        dto.setId(collaborator.getId());
        dto.setUserId(collaborator.getUser().getId());
        dto.setUsername(collaborator.getUser().getUsername());
        dto.setFullName(collaborator.getUser().getFullName());
        dto.setAvatarUrl(collaborator.getUser().getAvatarUrl());
        dto.setRole(collaborator.getRole());
        dto.setStatus(collaborator.getStatus());
        dto.setInvitedAt(collaborator.getInvitedAt());
        dto.setAcceptedAt(collaborator.getAcceptedAt());

        if (collaborator.getInvitedBy() != null) {
            dto.setInvitedByUsername(collaborator.getInvitedBy().getUsername());
        }

        if (collaborator.getRepository() != null) {
            dto.setRepositoryName(collaborator.getRepository().getName());
        }

        return dto;
    }
}
