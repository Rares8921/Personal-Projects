package com.example.ihas.dao;

import com.example.ihas.devices.SmartAirPurifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartAirPurifierDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartAirPurifier purifier, String userId) {
        String sql = "INSERT INTO smart_air_purifier (id, name, is_on, fan_speed, air_quality_index, filter_life, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, purifier.getId(), purifier.getName(), purifier.isOn(),
                purifier.getFanSpeed().name(), purifier.getAirQualityIndex(),
                purifier.getFilterLife(), userId);
    }

    public SmartAirPurifier findById(String id, String userId) {
        String sql = "SELECT id, name, is_on, fan_speed, air_quality_index, filter_life FROM smart_air_purifier WHERE id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartAirPurifier p = new SmartAirPurifier(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != p.isOn()) p.togglePower();
            p.setFanSpeed(SmartAirPurifier.FanSpeed.valueOf(rs.getString("fan_speed")));
            p.setAirQualityIndex(rs.getInt("air_quality_index"));
            p.setFilterLife(rs.getInt("filter_life"));
            return p;
        }, id, userId);
    }

    public List<SmartAirPurifier> findAll(String userId) {
        String sql = "SELECT id, name, is_on, fan_speed, air_quality_index, filter_life FROM smart_air_purifier WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartAirPurifier p = new SmartAirPurifier(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != p.isOn()) p.togglePower();
            p.setFanSpeed(SmartAirPurifier.FanSpeed.valueOf(rs.getString("fan_speed")));
            p.setAirQualityIndex(rs.getInt("air_quality_index"));
            p.setFilterLife(rs.getInt("filter_life"));
            return p;
        }, userId);
    }

    public void update(SmartAirPurifier purifier, String userId) {
        String sql = "UPDATE smart_air_purifier SET name = ?, is_on = ?, fan_speed = ?, air_quality_index = ?, filter_life = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, purifier.getName(), purifier.isOn(), purifier.getFanSpeed().name(),
                purifier.getAirQualityIndex(), purifier.getFilterLife(),
                purifier.getId(), userId);
    }

    public void delete(String id, String userId) {
        jdbcTemplate.update("DELETE FROM smart_air_purifier WHERE id = ? AND user_id = ?", id, userId);
    }
}
