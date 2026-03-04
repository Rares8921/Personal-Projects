package main.backend.repository;

import main.backend.model.Repository;
import main.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    Optional<Repository> findByOwnerUsernameAndName(String username, String name);
    List<Repository> findByOwnerUsername(String username);
    boolean existsByOwnerUsernameAndName(String username, String name);

    @Query("SELECT r FROM Repository r WHERE r.isPublic = true AND LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Repository> searchPublicRepositories(@Param("keyword") String keyword);

    void deleteAllByOwner(User owner);

    Page<Repository> findByIsPublicTrue(Pageable pageable);
}
