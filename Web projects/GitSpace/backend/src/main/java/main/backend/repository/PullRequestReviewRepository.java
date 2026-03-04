package main.backend.repository;

import main.backend.model.PullRequestReview;
import main.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestReviewRepository extends JpaRepository<PullRequestReview, Long> {
    void deleteAllByReviewer(User reviewer);
}