package com.example.ihas.dao;

import com.example.ihas.devices.SmartSprinkler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class SmartSprinklerDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartSprinkler sprinkler, String user_id) {
        String sql = """
            INSERT INTO smart_sprinkler (id, name, is_on, watering_duration, user_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                sprinkler.getId(),
                sprinkler.getName(),
                sprinkler.isOn(),
                sprinkler.getWateringDuration(),
                user_id
        );
    }

    public SmartSprinkler findById(String id, String user_id) {
        String sql = """
            SELECT id, name, is_on, watering_duration
            FROM smart_sprinkler
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartSprinkler sprinkler = new SmartSprinkler(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != sprinkler.isOn()) {
                sprinkler.togglePower();
            }
            sprinkler.setWateringDuration(rs.getInt("watering_duration"));
            return sprinkler;
        }, id, user_id);
    }

    public List<SmartSprinkler> findAllByUser(String user_id) {
        String sql = """
            SELECT id, name, is_on, watering_duration
            FROM smart_sprinkler
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartSprinkler sprinkler = new SmartSprinkler(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != sprinkler.isOn()) {
                sprinkler.togglePower();
            }
            sprinkler.setWateringDuration(rs.getInt("watering_duration"));
            return sprinkler;
        }, user_id);
    }

    public void update(SmartSprinkler sprinkler, String user_id) {
        String sql = """
            UPDATE smart_sprinkler
            SET name = ?, is_on = ?, watering_duration = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                sprinkler.getName(),
                sprinkler.isOn(),
                sprinkler.getWateringDuration(),
                sprinkler.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_sprinkler WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
