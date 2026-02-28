package com.example.ihas.dao;

import com.example.ihas.devices.SmartGarageDoor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartGarageDoorDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartGarageDoor door, String userId) {
        String sql = "INSERT INTO smart_garage_door (id, name, door_state, auto_close, auto_close_delay, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, door.getId(), door.getName(), door.getDoorState().name(),
                door.isAutoClose(), door.getAutoCloseDelay(), userId);
    }

    public SmartGarageDoor findById(String id, String userId) {
        String sql = "SELECT id, name, door_state, auto_close, auto_close_delay FROM smart_garage_door WHERE id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartGarageDoor d = new SmartGarageDoor(rs.getString("id"), rs.getString("name"));
            String state = rs.getString("door_state");
            if ("OPEN".equals(state)) d.open();
            d.setAutoClose(rs.getBoolean("auto_close"));
            d.setAutoCloseDelay(rs.getInt("auto_close_delay"));
            return d;
        }, id, userId);
    }

    public List<SmartGarageDoor> findAll(String userId) {
        String sql = "SELECT id, name, door_state, auto_close, auto_close_delay FROM smart_garage_door WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartGarageDoor d = new SmartGarageDoor(rs.getString("id"), rs.getString("name"));
            String state = rs.getString("door_state");
            if ("OPEN".equals(state)) d.open();
            d.setAutoClose(rs.getBoolean("auto_close"));
            d.setAutoCloseDelay(rs.getInt("auto_close_delay"));
            return d;
        }, userId);
    }

    public void update(SmartGarageDoor door, String userId) {
        String sql = "UPDATE smart_garage_door SET name = ?, door_state = ?, auto_close = ?, auto_close_delay = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, door.getName(), door.getDoorState().name(),
                door.isAutoClose(), door.getAutoCloseDelay(), door.getId(), userId);
    }

    public void delete(String id, String userId) {
        jdbcTemplate.update("DELETE FROM smart_garage_door WHERE id = ? AND user_id = ?", id, userId);
    }
}
