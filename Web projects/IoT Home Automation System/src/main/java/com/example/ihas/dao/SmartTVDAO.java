package com.example.ihas.dao;

import com.example.ihas.devices.SmartTV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartTVDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartTV tv, String userId) {
        String sql = "INSERT INTO smart_tv (id, name, is_on, volume, channel, input_source, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, tv.getId(), tv.getName(), tv.isOn(), tv.getVolume(), tv.getChannel(), tv.getInputSource(), userId);
    }

    public SmartTV findById(String id, String userId) {
        String sql = "SELECT id, name, is_on, volume, channel, input_source FROM smart_tv WHERE id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartTV tv = new SmartTV(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != tv.isOn()) tv.togglePower();
            tv.setVolume(rs.getInt("volume"));
            tv.setChannel(rs.getString("channel"));
            tv.setInputSource(rs.getString("input_source"));
            return tv;
        }, id, userId);
    }

    public List<SmartTV> findAll(String userId) {
        String sql = "SELECT id, name, is_on, volume, channel, input_source FROM smart_tv WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartTV tv = new SmartTV(rs.getString("id"), rs.getString("name"));
            if (rs.getBoolean("is_on") != tv.isOn()) tv.togglePower();
            tv.setVolume(rs.getInt("volume"));
            tv.setChannel(rs.getString("channel"));
            tv.setInputSource(rs.getString("input_source"));
            return tv;
        }, userId);
    }

    public void update(SmartTV tv, String userId) {
        String sql = "UPDATE smart_tv SET name = ?, is_on = ?, volume = ?, channel = ?, input_source = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, tv.getName(), tv.isOn(), tv.getVolume(), tv.getChannel(), tv.getInputSource(), tv.getId(), userId);
    }

    public void delete(String id, String userId) {
        jdbcTemplate.update("DELETE FROM smart_tv WHERE id = ? AND user_id = ?", id, userId);
    }
}
