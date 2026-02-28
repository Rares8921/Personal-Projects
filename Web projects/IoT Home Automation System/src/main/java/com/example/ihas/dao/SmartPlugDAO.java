package com.example.ihas.dao;

import com.example.ihas.devices.SmartPlug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartPlugDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartPlug plug, String user_id) {
        String sql = """
            INSERT INTO smart_plug (id, name, is_on, current_consumption, user_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                plug.getId(),
                plug.getName(),
                plug.isOn(),
                plug.getCurrentConsumption(),
                user_id
        );
    }

    public SmartPlug findById(String id, String user_id) {
        String sql = """
            SELECT id, name, is_on, current_consumption
            FROM smart_plug
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartPlug plug = new SmartPlug(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != plug.isOn()) {
                plug.togglePower();
            }
            plug.setCurrentConsumption(rs.getDouble("current_consumption"));
            return plug;
        }, id, user_id);
    }

    public List<SmartPlug> findAllByUser(String user_id) {
        String sql = """
            SELECT id, name, is_on, current_consumption
            FROM smart_plug
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartPlug plug = new SmartPlug(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != plug.isOn()) {
                plug.togglePower();
            }
            plug.setCurrentConsumption(rs.getDouble("current_consumption"));
            return plug;
        }, user_id);
    }

    public void update(SmartPlug plug, String user_id) {
        String sql = """
            UPDATE smart_plug
            SET name = ?, is_on = ?, current_consumption = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                plug.getName(),
                plug.isOn(),
                plug.getCurrentConsumption(),
                plug.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_plug WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
