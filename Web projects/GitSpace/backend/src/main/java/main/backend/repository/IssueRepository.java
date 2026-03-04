package main.backend.repository;

import main.backend.model.Issue;
import main.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByRepositoryId(Long repoId);
    List<Issue> findByRepositoryIdAndTitleContainingIgnoreCase(Long repoId, String query); // Issue 7

    List<Issue> findByRepositoryIdOrderByCreatedAtDesc(Long repoId);
    List<Issue> findByRepositoryIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(Long repoId, String query);

    ///repos/user/repo/issues/15 unde 15 e issueNumber
    Optional<Issue> findByRepositoryIdAndIssueNumber(Long repositoryId, Long issueNumber);

    // next id
    @Query("SELECT COALESCE(MAX(i.issueNumber), 0) FROM Issue i WHERE i.repository.id = :repoId")
    Long findMaxIssueNumberByRepositoryId(Long repoId);

    void deleteAllByAuthor(User author);
}
