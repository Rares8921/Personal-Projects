package com.example.ihas.dao;

import com.example.ihas.devices.SmartRobotVacuum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartRobotVacuumDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartRobotVacuum vacuum, String userId) {
        String sql = "INSERT INTO smart_robot_vacuum (id, name, is_on, mode, status, battery_level, cleaned_area, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, vacuum.getId(), vacuum.getName(), vacuum.isOn(),
                vacuum.getMode().name(), vacuum.getVacuumStatus().name(),
                vacuum.getBatteryLevel(), vacuum.getCleanedArea(), userId);
    }

    public SmartRobotVacuum findById(String id, String userId) {
        String sql = "SELECT id, name, is_on, mode, status, battery_level, cleaned_area FROM smart_robot_vacuum WHERE id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartRobotVacuum v = new SmartRobotVacuum(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != v.isOn()) v.togglePower();
            v.setMode(SmartRobotVacuum.CleanMode.valueOf(rs.getString("mode")));
            v.setBatteryLevel(rs.getInt("battery_level"));
            v.setCleanedArea(rs.getInt("cleaned_area"));
            return v;
        }, id, userId);
    }

    public List<SmartRobotVacuum> findAll(String userId) {
        String sql = "SELECT id, name, is_on, mode, status, battery_level, cleaned_area FROM smart_robot_vacuum WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartRobotVacuum v = new SmartRobotVacuum(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != v.isOn()) v.togglePower();
            v.setMode(SmartRobotVacuum.CleanMode.valueOf(rs.getString("mode")));
            v.setBatteryLevel(rs.getInt("battery_level"));
            v.setCleanedArea(rs.getInt("cleaned_area"));
            return v;
        }, userId);
    }

    public void update(SmartRobotVacuum vacuum, String userId) {
        String sql = "UPDATE smart_robot_vacuum SET name = ?, is_on = ?, mode = ?, status = ?, battery_level = ?, cleaned_area = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, vacuum.getName(), vacuum.isOn(), vacuum.getMode().name(),
                vacuum.getVacuumStatus().name(), vacuum.getBatteryLevel(), vacuum.getCleanedArea(),
                vacuum.getId(), userId);
    }

    public void delete(String id, String userId) {
        jdbcTemplate.update("DELETE FROM smart_robot_vacuum WHERE id = ? AND user_id = ?", id, userId);
    }
}
