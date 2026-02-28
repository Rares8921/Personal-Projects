package com.example.ihas.dao;

import com.example.ihas.devices.SmartCarCharger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartCarChargerDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartCarCharger charger, String user_id) {
        String sql = """
            INSERT INTO smart_car_charger (id, name, is_charging, current, voltage, user_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                charger.getId(),
                charger.getName(),
                charger.isCharging(),
                charger.getCurrent(), charger.getVoltage(),
                user_id
        );
    }

    public SmartCarCharger findById(String id, String user_id) {
        String sql = """
            SELECT id, name, is_charging, current, voltage
            FROM smart_car_charger
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SmartCarCharger c = new SmartCarCharger(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_charging") != c.isCharging()) c.togglePower();
            c.setCurrent(rs.getDouble("current"));
            c.setVoltage(rs.getDouble("voltage"));
            return c;
        }, id, user_id);
    }

    public List<SmartCarCharger> findAll(String user_id) {
        String sql = """
            SELECT id, name, is_charging, current, voltage
            FROM smart_car_charger
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SmartCarCharger c = new SmartCarCharger(
                    rs.getString("id"),
                    rs.getString("name")
            );
            if (rs.getBoolean("is_charging") != c.isCharging()) c.togglePower();
            c.setCurrent(rs.getDouble("current"));
            c.setVoltage(rs.getDouble("voltage"));
            return c;
        }, user_id);
    }

    public void update(SmartCarCharger charger, String user_id) {
        String sql = """
            UPDATE smart_car_charger
            SET name = ?, is_charging = ?, current = ?, voltage = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                charger.getName(),
                charger.isCharging(),
                charger.getCurrent(),
                charger.getVoltage(),
                charger.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_car_charger WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
