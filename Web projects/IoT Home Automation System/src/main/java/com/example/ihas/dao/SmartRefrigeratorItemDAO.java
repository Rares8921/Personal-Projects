package com.example.ihas.dao;

import com.example.ihas.devices.SmartRefrigeratorItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SmartRefrigeratorItemDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartRefrigeratorItem item) {
        String sql = "INSERT INTO smart_refrigerator_item (name, expiry_date, refrigerator_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, item.getName(), Date.valueOf(item.getExpiryDate()), item.getRefrigeratorId());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM smart_refrigerator_item WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<SmartRefrigeratorItem> findByRefrigeratorId(String refrigeratorId) {
        String sql = """
            SELECT id, name, expiry_date, refrigerator_id
            FROM smart_refrigerator_item
            WHERE refrigerator_id = ?
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartRefrigeratorItem item = new SmartRefrigeratorItem();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
            item.setRefrigeratorId(rs.getString("refrigerator_id"));
            return item;
        }, refrigeratorId);
    }

    public void deleteExpired(String refrigeratorId) {
        String sql = """
            DELETE FROM smart_refrigerator_item
            WHERE refrigerator_id = ? AND expiry_date < ?
        """;
        jdbcTemplate.update(sql, refrigeratorId, Date.valueOf(LocalDate.now()));
    }
}
