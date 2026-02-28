package com.example.ihas.devices;

public sealed interface SmartDevice
        permits SmartLight, SmartThermostat, SmartCamera, SmartPlug, SmartSensor, SmartDoorLock, SmartCurtains, SmartSprinkler, SmartRefrigerator, SmartOven, SmartCarCharger, SmartAlarmSystem, SmartHub, SmartAssistant, SmartTV, SmartWasher, SmartRobotVacuum, SmartGarageDoor, SmartAirPurifier {

    String getId();
    String getName();
    boolean isOnline();
    void togglePower();
    String getStatus();
}

