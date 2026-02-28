package com.example.ihas.services;

import com.example.ihas.dao.UserDAO;
import com.example.ihas.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private EmailService emailService;
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserDAO _userDAO, BCryptPasswordEncoder _passwordEncoder, EmailService _emailService) {
        userDAO = _userDAO;
        passwordEncoder = _passwordEncoder;
        emailService = _emailService;
    }

    public void registerUser(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        String verificationToken = UUID.randomUUID().toString();

        User user = new User(email, encodedPassword, verificationToken);
        userDAO.save(user);

        emailService.sendVerificationEmail(email, verificationToken);
    }

    public boolean confirmEmail(String token) {
        Optional<User> userOpt = userDAO.findByToken(token);
        if (userOpt.isPresent()) {
            userDAO.enableUser(userOpt.get().getEmail());
            return true;
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public boolean isUserVerified(String email) {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.isEnabled();
        }
        return false;
    }
}
