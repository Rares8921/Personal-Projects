package main.backend.service;

import main.backend.model.Activity;
import main.backend.model.User;
import main.backend.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void log(User user, String description) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setDescription(description);
        activity.setTimestamp(Instant.now());
        activityRepository.save(activity);
    }

    public List<Activity> getFeedForUser(Long userId) {
        return activityRepository.findFeedByUserId(userId);
    }

    public List<Activity> getPublicActivity(String username) {
        return activityRepository.findByUserUsernameOrderByTimestampDesc(username);
    }
}
