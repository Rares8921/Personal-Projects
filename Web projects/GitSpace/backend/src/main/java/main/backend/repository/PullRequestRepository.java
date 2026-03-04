package main.backend.repository;

import main.backend.model.PullRequest;
import main.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    List<PullRequest> findByRepositoryId(Long repoId);

    List<PullRequest> findByRepositoryIdOrderByIdDesc(Long repoId);

    Optional<PullRequest> findByRepositoryIdAndPrNumber(Long repoId, Long prNumber);

    @Query("SELECT COALESCE(MAX(p.prNumber), 0) FROM PullRequest p WHERE p.repository.id = :repoId")
    Long findMaxPrNumberByRepositoryId(Long repoId);

    void deleteAllByAuthor(User author);
}
