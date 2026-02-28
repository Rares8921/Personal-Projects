package com.example.ihas.dao;

import com.example.ihas.devices.SmartWasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartWasherDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartWasher washer, String userId) {
        String sql = "INSERT INTO smart_washer (id, name, is_on, mode, temperature, spin_speed, remaining_minutes, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, washer.getId(), washer.getName(), washer.isOn(),
                washer.getMode().name(), washer.getTemperature(), washer.getSpinSpeed(),
                washer.getRemainingMinutes(), userId);
    }

    public SmartWasher findById(String id, String userId) {
        String sql = "SELECT id, name, is_on, mode, temperature, spin_speed, remaining_minutes FROM smart_washer WHERE id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartWasher w = new SmartWasher(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != w.isOn()) w.togglePower();
            w.setMode(SmartWasher.WashMode.valueOf(rs.getString("mode")));
            w.setTemperature(rs.getInt("temperature"));
            w.setSpinSpeed(rs.getInt("spin_speed"));
            w.setRemainingMinutes(rs.getInt("remaining_minutes"));
            return w;
        }, id, userId);
    }

    public List<SmartWasher> findAll(String userId) {
        String sql = "SELECT id, name, is_on, mode, temperature, spin_speed, remaining_minutes FROM smart_washer WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartWasher w = new SmartWasher(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != w.isOn()) w.togglePower();
            w.setMode(SmartWasher.WashMode.valueOf(rs.getString("mode")));
            w.setTemperature(rs.getInt("temperature"));
            w.setSpinSpeed(rs.getInt("spin_speed"));
            w.setRemainingMinutes(rs.getInt("remaining_minutes"));
            return w;
        }, userId);
    }

    public void update(SmartWasher washer, String userId) {
        String sql = "UPDATE smart_washer SET name = ?, is_on = ?, mode = ?, temperature = ?, spin_speed = ?, remaining_minutes = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, washer.getName(), washer.isOn(), washer.getMode().name(),
                washer.getTemperature(), washer.getSpinSpeed(), washer.getRemainingMinutes(),
                washer.getId(), userId);
    }

    public void delete(String id, String userId) {
        jdbcTemplate.update("DELETE FROM smart_washer WHERE id = ? AND user_id = ?", id, userId);
    }
}
