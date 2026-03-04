package main.backend.repository;

import main.backend.model.Activity;
import main.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> { // Issue 6
    @Query("SELECT a FROM Activity a " +
            "JOIN UserFollow f ON a.user.id = f.following.id " +
            "WHERE f.follower.id = :userId " +
            "ORDER BY a.timestamp DESC")
    List<Activity> findFeedByUserId(Long userId);

    // Activitatile unui user specific
    List<Activity> findByUserUsernameOrderByTimestampDesc(String username);

    void deleteAllByUser(User user);
}
