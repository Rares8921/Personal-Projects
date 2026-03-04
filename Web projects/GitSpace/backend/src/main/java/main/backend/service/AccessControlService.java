package main.backend.service;

import main.backend.model.Repository;
import main.backend.model.RepositoryCollaborator;
import main.backend.model.Role;
import main.backend.model.User;
import main.backend.model.enums.CollaboratorRoleEnum;
import main.backend.model.enums.InvitationStatusEnum;
import main.backend.repository.RepositoryCollaboratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccessControlService {

    private final RepositoryCollaboratorRepository collaboratorRepository;

    @Autowired
    public AccessControlService(RepositoryCollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    public boolean canViewRepo(User user, Repository repo) {
        if (repo.isPublic()) {
            return true;
        }

        if (user == null) {
            return false;
        }

        if (repo.getOwner().getId().equals(user.getId())) {
            return true;
        }

        Optional<RepositoryCollaborator> collaborator =
            collaboratorRepository.findByRepositoryAndUser(repo, user);

        return collaborator.isPresent()
            && collaborator.get().getStatus() == InvitationStatusEnum.ACCEPTED;
    }

    public boolean canWriteRepo(User user, Repository repo) {
        if (user == null) {
            return false;
        }

        if (isAdmin(user)) return true;

        if (repo.isArchived()) return false;

        if (repo.getOwner().getId().equals(user.getId())) {
            return true;
        }

        Optional<RepositoryCollaborator> collaborator =
            collaboratorRepository.findByRepositoryAndUser(repo, user);

        return collaborator.isPresent()
            && collaborator.get().getStatus() == InvitationStatusEnum.ACCEPTED
            && collaborator.get().getRole() == CollaboratorRoleEnum.WRITE;
    }

    public boolean isOwner(User user, Repository repo) {
        if (user == null || repo == null) return false;
        return repo.getOwner().getId().equals(user.getId());
    }

    public boolean isAdmin(User user) {
        if (user == null) return false;

        return user.getRole() == Role.ROLE_ADMIN;
    }
}
