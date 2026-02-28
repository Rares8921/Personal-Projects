package com.example.ihas.dao;

import com.example.ihas.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate _jdbcTemplate) {
        jdbcTemplate = _jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User(rs.getString("email"), rs.getString("password"), rs.getString("verification_token"));
        user.setId(UUID.fromString(rs.getString("user_id")));
        user.setEnabled(rs.getBoolean("enabled"));
        return user;
    };

    public void save(User user) {
        String sql = "INSERT INTO users (user_id, email, password, enabled, verification_token) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId().toString(), user.getEmail(), user.getPassword(), user.isEnabled(), user.getVerificationToken());
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, userRowMapper, email).stream().findFirst();
    }

    public Optional<User> findById(String user_id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.query(sql, userRowMapper, user_id).stream().findFirst();
    }

    public Optional<User> findByToken(String token) {
        String sql = "SELECT * FROM users WHERE verification_token = ?";
        return jdbcTemplate.query(sql, userRowMapper, token).stream().findFirst();
    }

    public void enableUser(String email) {
        String sql = "UPDATE users SET enabled = TRUE WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }
}
