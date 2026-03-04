package main.backend.controller;

import main.backend.dto.user.UserProfileDto;
import main.backend.dto.user.UserSettingsDto;
import main.backend.model.User;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Handles user profiles and settings.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * URL: /api/users/search?query=alex
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.search(query));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateUserSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserSettingsDto dto
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        userService.updateSettings(user.getId(), dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String username
    ) {
        User currentUser = userService.findByUsername(userDetails.getUsername());

        String statusMessage = userService.follow(currentUser.getId(), username);

        return ResponseEntity.ok(Collections.singletonMap("message", statusMessage));
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<?> unfollowUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String username
    ) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        userService.unfollow(currentUser.getId(), username);
        return ResponseEntity.ok("Unfollowed successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
