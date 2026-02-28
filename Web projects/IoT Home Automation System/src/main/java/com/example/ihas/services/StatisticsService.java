package com.example.ihas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getStatistics(String user_id) {
        Map<String, Object> stats = new HashMap<>();

        int thermostatTotal = count("smart_thermostat", user_id);
        int lightTotal = count("smart_lights", user_id);
        int ovenTotal = count("smart_oven", user_id);
        int alarmTotal = count("smart_alarm_system", user_id);
        int assistantTotal = count("smart_assistant", user_id);
        int hubTotal = count("smart_hub", user_id);
        int doorLockTotal = count("smart_door_lock", user_id);
        int plugTotal = count("smart_plug", user_id);
        int carChargerTotal = count("smart_car_charger", user_id);
        int cameraTotal = count("smart_camera", user_id);
        int curtainTotal = count("smart_curtains", user_id);
        int fridgeTotal = count("smart_refrigerator", user_id);
        int sprinklerTotal = count("smart_sprinkler", user_id);
        int sensorTotal = count("smart_sensor", user_id);
        int tvTotal = count("smart_tv", user_id);
        int washerTotal = count("smart_washer", user_id);
        int vacuumTotal = count("smart_robot_vacuum", user_id);
        int garageDoorTotal = count("smart_garage_door", user_id);
        int airPurifierTotal = count("smart_air_purifier", user_id);

        int totalDevices = thermostatTotal + lightTotal + ovenTotal + alarmTotal + assistantTotal + hubTotal +
                doorLockTotal + plugTotal + carChargerTotal + cameraTotal + curtainTotal + fridgeTotal +
                sprinklerTotal + sensorTotal + tvTotal + washerTotal + vacuumTotal + garageDoorTotal + airPurifierTotal;

        int thermostatOnline = countOnline("smart_thermostat", "is_on", user_id);
        int lightOnline = countOnline("smart_lights", "is_on", user_id);
        int ovenOnline = countOnline("smart_oven", "is_on", user_id);
        int alarmOnline = countOnline("smart_alarm_system", "is_armed", user_id);
        int doorLockOnline = countOnline("smart_door_lock", "locked", user_id);
        int plugOnline = countOnline("smart_plug", "is_on", user_id);
        int chargerOnline = countOnline("smart_car_charger", "is_charging", user_id);
        int cameraOnline = countOnline("smart_camera", "is_recording", user_id);
        int sprinklerOnline = countOnline("smart_sprinkler", "is_on", user_id);
        int tvOnline = countOnline("smart_tv", "is_on", user_id);
        int washerOnline = countOnline("smart_washer", "is_on", user_id);
        int vacuumOnline = countOnline("smart_robot_vacuum", "is_on", user_id);
        int airPurifierOnline = countOnline("smart_air_purifier", "is_on", user_id);

        int onlineDevices = thermostatOnline + lightOnline + ovenOnline + alarmOnline +
                plugOnline + chargerOnline + cameraOnline + sprinklerOnline + assistantTotal + hubTotal +
                tvOnline + washerOnline + vacuumOnline + garageDoorTotal + airPurifierOnline + sensorTotal +
                curtainTotal + doorLockOnline + fridgeTotal;

        stats.put("totalDevices", totalDevices);
        stats.put("onlineDevices", onlineDevices);

        return stats;
    }

    private int count(String table, String user_id) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, user_id);
    }

    private int countOnline(String table, String column, String user_id) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = true AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, user_id);
    }
}
