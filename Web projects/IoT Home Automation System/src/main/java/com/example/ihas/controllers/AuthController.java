package com.example.ihas.controllers;

import com.example.ihas.config.JwtUtil;
import com.example.ihas.models.User;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.UserService;
import com.example.ihas.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final UserService userService;

    @Autowired
    private AuditService auditService;

    public AuthController(UserService _userService, JwtUtil _tokenUtil) {
        userService = _userService;
        jwtUtil = _tokenUtil;
    }

    @PostMapping("/register_user")
    @ResponseBody
    public ResponseEntity<String> register(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
        try {
            userService.registerUser(email, password);

            String ip = IpUtil.getClientIp(request);
            String message = String.format("Registered user: %s from IP: %s", email, ip);
            auditService.log(message);

            return ResponseEntity.ok("Registration successful! Please check your email to verify your account.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during registration: " + e.getMessage());
        }
    }

    @GetMapping("/verify_user")
    public RedirectView verify(@RequestParam String token) {
        if (userService.confirmEmail(token)) {
            return new RedirectView("/verify-success.html");
        } else {
            return new RedirectView("/verify-fail.html");
        }
    }

    @PostMapping("/login_user")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "User not found"));
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body(Map.of("message", "Account not verified. Check your email!"));
        }

        if (!userService.checkPassword(password, user.getPassword())) {
            return ResponseEntity.status(403).body(Map.of("message", "Invalid credentials!"));
        }

        if (!userService.isUserVerified(email)) {
            return ResponseEntity.status(401).body(Map.of("message", "Email not verified"));
        }

        String ip = IpUtil.getClientIp(request);
        auditService.log(String.format("Login success for user: %s from IP: %s", email, ip));

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("message", "Login success", "token", token));
    }
}
