package com.example.ihas.devices;

import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

public final class SmartRefrigerator implements SmartDevice {
    private final String id;
    private final String name;

    @Getter
    private double temperature;
    @Getter
    private boolean doorOpen;

    // data de expirare -> lista de produse
    @Getter
    private final Map<LocalDate, List<String>> inventory;

    @Getter
    private final List<String> eventLog;

    public SmartRefrigerator(String _id, String _name) {
        id = _id;
        name = _name;
        temperature = 4.0;
        doorOpen = false;
        inventory = new TreeMap<>();
        eventLog = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isOnline() { return true; }

    @Override
    public void togglePower() {
        doorOpen = !doorOpen;
        eventLog.add("Door " + (doorOpen ? "opened" : "closed"));
    }

    public void setTemperature(double _temperature) {
        if (_temperature < -5 || _temperature > 10)
            throw new IllegalArgumentException("Temperature must be between -5°C and 10°C");
        temperature = _temperature;
        eventLog.add("Temperature set to " + _temperature);
    }

    public void addItem(String item, LocalDate expiry) {
        inventory.computeIfAbsent(expiry, k -> new ArrayList<>()).add(item);
        eventLog.add("Added item: " + item + " (expires: " + expiry + ")");
    }

    public void removeItem(String item) {
        for (Map.Entry<LocalDate, List<String>> entry : inventory.entrySet()) {
            if (entry.getValue().remove(item)) {
                eventLog.add("Removed item: " + item);
                if (entry.getValue().isEmpty()) {
                    inventory.remove(entry.getKey());
                }
                return;
            }
        }
        eventLog.add("Attempted to remove item not found: " + item);
    }

    public String checkExpiredItems() {
        StringBuilder sb = new StringBuilder();
        LocalDate today = LocalDate.now();
        for (Map.Entry<LocalDate, List<String>> entry : inventory.entrySet()) {
            if (entry.getKey().isBefore(today)) {
                for (String item : entry.getValue()) {
                    sb.append(item).append(" expired; ");
                }
            }
        }
        String result = sb.toString();
        eventLog.add("Checked expired items: " + result);
        return result;
    }

    @Override
    public String getStatus() {
        int inventorySize = inventory.values().stream().mapToInt(List::size).sum();
        return String.format("SmartRefrigerator[id=%s, name=%s, temperature=%.1f, doorOpen=%s, inventorySize=%d]",
                id, name, temperature, doorOpen, inventorySize);
    }
}
