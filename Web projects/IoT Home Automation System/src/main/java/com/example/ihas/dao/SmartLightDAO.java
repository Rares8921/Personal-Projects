package com.example.ihas.dao;

import com.example.ihas.devices.SmartLight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartLightDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartLight light, String user_id) {
        String sql = """
            INSERT INTO smart_lights (id, name, is_on, brightness, color, user_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                light.getId(),
                light.getName(),
                light.isOn(),
                light.getBrightness(),
                light.getColor(),
                user_id
        );
    }

    public SmartLight findById(String id, String user_id) {
        String sql = """
            SELECT id, name, is_on, brightness, color
            FROM smart_lights
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartLight l = new SmartLight(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != l.isOn()) l.togglePower();
            l.setBrightness(rs.getInt("brightness"));
            l.setColor(rs.getString("color"));
            return l;
        }, id, user_id);
    }

    public List<SmartLight> findAll(String user_id) {
        String sql = """
            SELECT id, name, is_on, brightness, color
            FROM smart_lights
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartLight l = new SmartLight(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != l.isOn()) l.togglePower();
            l.setBrightness(rs.getInt("brightness"));
            l.setColor(rs.getString("color"));
            return l;
        }, user_id);
    }

    public void update(SmartLight light, String user_id) {
        String sql = """
            UPDATE smart_lights
            SET name = ?, is_on = ?, brightness = ?, color = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                light.getName(),
                light.isOn(),
                light.getBrightness(),
                light.getColor(),
                light.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_lights WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
