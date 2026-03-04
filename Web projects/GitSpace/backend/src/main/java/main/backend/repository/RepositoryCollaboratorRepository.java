package main.backend.repository;

import main.backend.model.RepositoryCollaborator;
import main.backend.model.User;
import main.backend.model.enums.InvitationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import main.backend.model.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository // conflict cu bean Repository
public interface RepositoryCollaboratorRepository extends JpaRepository<RepositoryCollaborator, Long> {

        List<RepositoryCollaborator> findByRepository(Repository repository);

        List<RepositoryCollaborator> findByRepositoryAndStatus(
                        Repository repository,
                        InvitationStatusEnum status);

        Optional<RepositoryCollaborator> findByRepositoryAndUser(
                        Repository repository,
                        User user);

        boolean existsByRepositoryAndUserAndStatus(
                        Repository repository,
                        User user,
                        InvitationStatusEnum status);

        List<RepositoryCollaborator> findByUserAndStatus(
                        User user,
                        InvitationStatusEnum status);

        @Query("SELECT rc.repository FROM RepositoryCollaborator rc WHERE rc.user = :user AND rc.status = 'ACCEPTED'")
        List<Repository> findRepositoriesByCollaborator(@Param("user") User user);

        @Query("SELECT COUNT(rc) FROM RepositoryCollaborator rc WHERE rc.repository = :repo AND rc.status = 'ACCEPTED'")
        long countActiveCollaborators(@Param("repo") Repository repository);

        @Query("SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END FROM RepositoryCollaborator rc " +
                        "WHERE rc.repository = :repo AND rc.user = :user AND rc.status = 'ACCEPTED' " +
                        "AND rc.role = 'WRITE'")
        boolean hasWriteAccess(@Param("repo") Repository repository, @Param("user") User user);
}
