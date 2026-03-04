package main.backend.repository;

import main.backend.model.Comment;
import main.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // comentariile unui issue ordonate cresc
    List<Comment> findByIssueIdOrderByCreatedAtAsc(Long issueId);
    void deleteAllByAuthor(User author);
    List<Comment> findByPullRequestIdOrderByCreatedAtAsc(Long pullRequestId);
}