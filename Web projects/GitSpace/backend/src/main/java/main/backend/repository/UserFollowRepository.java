package main.backend.repository;

import main.backend.model.User;
import main.backend.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> { // Issue 2
    long countByFollowing_Id(Long userId); // followers
    long countByFollower_Id(Long userId); // following

    // follow/unfollow
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteAllByFollower(User follower);
    void deleteAllByFollowing(User following);
}
