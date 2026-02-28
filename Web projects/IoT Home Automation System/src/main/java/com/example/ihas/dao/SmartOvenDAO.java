package com.example.ihas.dao;

import com.example.ihas.devices.SmartOven;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartOvenDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartOven oven, String user_id) {
        String sql = """
            INSERT INTO smart_oven (id, name, is_on, temperature, timer, preheat, user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                oven.getId(),
                oven.getName(),
                oven.isOnline(),
                oven.getTemperature(),
                oven.getTimer(),
                oven.isPreheat(),
                user_id
        );
    }

    public SmartOven findById(String id, String user_id) {
        String sql = """
            SELECT id, name, is_on, temperature, timer, preheat
            FROM smart_oven
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartOven o = new SmartOven(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != o.isOnline()) o.togglePower();
            o.setTemperature(rs.getDouble("temperature"));
            o.setTimer(rs.getInt("timer"));
            o.setPreheat(rs.getBoolean("preheat"));
            return o;
        }, id, user_id);
    }

    public List<SmartOven> findAll(String user_id) {
        String sql = """
            SELECT id, name, is_on, temperature, timer, preheat
            FROM smart_oven
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartOven o = new SmartOven(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_on") != o.isOnline()) o.togglePower();
            o.setTemperature(rs.getDouble("temperature"));
            o.setTimer(rs.getInt("timer"));
            o.setPreheat(rs.getBoolean("preheat"));
            return o;
        }, user_id);
    }

    public void update(SmartOven oven, String user_id) {
        String sql = """
            UPDATE smart_oven
            SET name = ?, is_on = ?, temperature = ?, timer = ?, preheat = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                oven.getName(),
                oven.isOnline(),
                oven.getTemperature(),
                oven.getTimer(),
                oven.isPreheat(),
                oven.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_oven WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
