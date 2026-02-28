package com.example.ihas.dao;

import com.example.ihas.devices.SmartRefrigerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartRefrigeratorDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartRefrigerator fridge, String user_id) {
        String sql = """
            INSERT INTO smart_refrigerator (id, name, temperature, door_open, user_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                fridge.getId(),
                fridge.getName(),
                fridge.getTemperature(),
                fridge.isDoorOpen(),
                user_id
        );
    }

    public SmartRefrigerator findById(String id, String user_id) {
        String sql = """
            SELECT id, name, temperature, door_open
            FROM smart_refrigerator
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartRefrigerator fr = new SmartRefrigerator(
                    rs.getString("id"),
                    rs.getString("name")
            );
            fr.setTemperature(rs.getDouble("temperature"));
            if (rs.getBoolean("door_open") != fr.isDoorOpen()) {
                fr.togglePower();
            }
            return fr;
        }, id, user_id);
    }

    public List<SmartRefrigerator> findAllByUser(String user_id) {
        String sql = """
            SELECT id, name, temperature, door_open
            FROM smart_refrigerator
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartRefrigerator fr = new SmartRefrigerator(
                    rs.getString("id"),
                    rs.getString("name")
            );
            fr.setTemperature(rs.getDouble("temperature"));
            if (rs.getBoolean("door_open") != fr.isDoorOpen()) {
                fr.togglePower();
            }
            return fr;
        }, user_id);
    }

    public void update(SmartRefrigerator fridge, String user_id) {
        String sql = """
            UPDATE smart_refrigerator
            SET name = ?, temperature = ?, door_open = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                fridge.getName(),
                fridge.getTemperature(),
                fridge.isDoorOpen(),
                fridge.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_refrigerator WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
