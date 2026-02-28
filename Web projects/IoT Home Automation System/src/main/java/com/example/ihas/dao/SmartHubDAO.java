package com.example.ihas.dao;

import com.example.ihas.devices.SmartHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartHubDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartHub hub, String user_id) {
        String sql = """
            INSERT INTO smart_hub (id, name, user_id)
            VALUES (?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                hub.getId(),
                hub.getName(),
                user_id
        );
    }

    public SmartHub findById(String id, String user_id) {
        String sql = """
            SELECT id, name
            FROM smart_hub
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new SmartHub(
                        rs.getString("id"),
                        rs.getString("name")
                ), id, user_id);
    }

    public List<SmartHub> findAll(String user_id) {
        String sql = """
            SELECT id, name
            FROM smart_hub
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new SmartHub(
                        rs.getString("id"),
                        rs.getString("name")
                ), user_id);
    }

    public void update(SmartHub hub, String user_id) {
        String sql = """
            UPDATE smart_hub
            SET name = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                hub.getName(),
                hub.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_hub WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
