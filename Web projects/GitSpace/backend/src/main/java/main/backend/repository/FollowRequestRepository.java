package main.backend.repository;

import main.backend.model.FollowRequest;
import main.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
    boolean existsByFollowerAndTarget(User follower, User target);
}