package main.backend.controller;

import main.backend.model.User;
import main.backend.service.ActivityService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Handles Basic Activity Feed.
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityService activityService;
    private final UserService userService;

    @Autowired
    public ActivityController(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getFeedForUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(activityService.getFeedForUser(user.getId()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getPublicActivityForUser(@PathVariable String username) {
        return ResponseEntity.ok(activityService.getPublicActivity(username));
    }
}