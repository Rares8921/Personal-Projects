package com.example.ihas.dao;

import com.example.ihas.devices.SmartAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartAssistantDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartAssistant assistant, String user_id) {
        String sql = """
            INSERT INTO smart_assistant (id, name, hub_id, user_id)
            VALUES (?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql, assistant.getId(), assistant.getName(), assistant.getHubId(), user_id);
    }

    public SmartAssistant findById(String id, String user_id) {
        String sql = """
            SELECT id, name, hub_id
            FROM smart_assistant
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new SmartAssistant(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("hub_id")
                ), id, user_id);
    }

    public List<SmartAssistant> findAll(String user_id) {
        String sql = """
            SELECT id, name, hub_id
            FROM smart_assistant
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new SmartAssistant(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("hub_id")
                ), user_id);
    }

    public void update(SmartAssistant assistant, String user_id) {
        String sql = """
            UPDATE smart_assistant
            SET name = ?, hub_id = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql, assistant.getName(), assistant.getHubId(), assistant.getId(), user_id);
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_assistant WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
