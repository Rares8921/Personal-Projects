package main.backend.controller;

import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @DeleteMapping("/users/{userId}/avatar")
    public ResponseEntity<Void> deleteUserAvatar(@PathVariable Long userId) {
        userService.removeAvatarById(userId);
        return ResponseEntity.noContent().build();
    }
}
