package com.example.ihas.dao;

import com.example.ihas.devices.SmartSensor;
import com.example.ihas.devices.SmartSensor.SensorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmartSensorDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(SmartSensor sensor, String user_id) {
        String sql = """
            INSERT INTO smart_sensor (id, name, sensor_type, last_reading, user_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                sensor.getId(),
                sensor.getName(),
                sensor.getSensorType().name(),
                sensor.getLastReading(),
                user_id
        );
    }

    public SmartSensor findById(String id, String user_id) {
        String sql = """
            SELECT id, name, sensor_type, last_reading
            FROM smart_sensor
            WHERE id = ? AND user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SensorType type = SensorType.valueOf(rs.getString("sensor_type"));
            SmartSensor sensor = new SmartSensor(
                    rs.getString("id"),
                    rs.getString("name"),
                    type
            );
            sensor.setLastReading(rs.getDouble("last_reading"));
            return sensor;
        }, id, user_id);
    }

    public List<SmartSensor> findAllByUser(String user_id) {
        String sql = """
            SELECT id, name, sensor_type, last_reading
            FROM smart_sensor
            WHERE user_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SensorType type = SensorType.valueOf(rs.getString("sensor_type"));
            SmartSensor sensor = new SmartSensor(
                    rs.getString("id"),
                    rs.getString("name"),
                    type
            );
            sensor.setLastReading(rs.getDouble("last_reading"));
            return sensor;
        }, user_id);
    }

    public void update(SmartSensor sensor, String user_id) {
        String sql = """
            UPDATE smart_sensor
            SET name = ?, sensor_type = ?, last_reading = ?
            WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql,
                sensor.getName(),
                sensor.getSensorType().name(),
                sensor.getLastReading(),
                sensor.getId(),
                user_id
        );
    }

    public void delete(String id, String user_id) {
        String sql = "DELETE FROM smart_sensor WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user_id);
    }
}
