package com.example.ihas.dao;

import com.example.ihas.devices.SmartCurtains;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartCurtainsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartCurtains curtains, String user_id) {
        String sql = """
            INSERT INTO smart_curtains (id, name, position, user_id)
            VALUES (?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                curtains.getId(),
                curtains.getName(),
                curtains.getPosition(),
                user_id
        );
    }

    public SmartCurtains findById(String id, String user_id) {
        String sql = """
        SELECT id, name, position
        FROM smart_curtains
        WHERE id = ? AND user_id = ?
    """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartCurtains c = new SmartCurtains(
                    rs.getString("id"),
                    rs.getString("name")
            );
            c.setPosition(rs.getInt("position"));
            return c;
        }, id, user_id);
    }

    public List<SmartCurtains> findAll(String user_id) {
        String sql = """
        SELECT id, name, position
        FROM smart_curtains
        WHERE user_id = ?
    """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartCurtains c = new SmartCurtains(
                    rs.getString("id"),
                    rs.getString("name")
            );
            c.setPosition(rs.getInt("position"));
            return c;
        }, user_id);
    }

    public void update(SmartCurtains curtains, String user_id) {
        String sql = """
            UPDATE smart_curtains
            SET name = ?, position = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                curtains.getName(),
                curtains.getPosition(),
                curtains.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_curtains WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
