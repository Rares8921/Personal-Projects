package com.example.ihas;

import com.example.ihas.devices.SmartTV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartTVTest {

    @Test
    public void testTogglePower() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        Assertions.assertFalse(tv.isOn());
        tv.togglePower();
        Assertions.assertTrue(tv.isOn());
        tv.togglePower();
        Assertions.assertFalse(tv.isOn());
    }

    @Test
    public void testDefaultValues() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        Assertions.assertEquals("TV001", tv.getId());
        Assertions.assertEquals("Living Room TV", tv.getName());
        Assertions.assertFalse(tv.isOn());
        Assertions.assertEquals(30, tv.getVolume());
        Assertions.assertEquals("1", tv.getChannel());
        Assertions.assertEquals("HDMI1", tv.getInputSource());
    }

    @Test
    public void testSetVolume() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        tv.setVolume(75);
        Assertions.assertEquals(75, tv.getVolume());
    }

    @Test
    public void testSetVolumeInvalid() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        Assertions.assertThrows(IllegalArgumentException.class, () -> tv.setVolume(-1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> tv.setVolume(101));
    }

    @Test
    public void testMute() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        tv.setVolume(50);
        tv.mute();
        Assertions.assertEquals(0, tv.getVolume());
    }

    @Test
    public void testSetChannel() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        tv.setChannel("HBO");
        Assertions.assertEquals("HBO", tv.getChannel());
    }

    @Test
    public void testSetInputSource() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        tv.setInputSource("STREAMING");
        Assertions.assertEquals("STREAMING", tv.getInputSource());
    }

    @Test
    public void testEventLog() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        tv.togglePower();
        tv.setVolume(50);
        tv.mute();
        Assertions.assertEquals(3, tv.getEventLog().size());
    }

    @Test
    public void testGetStatus() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        String status = tv.getStatus();
        Assertions.assertTrue(status.contains("TV001"));
        Assertions.assertTrue(status.contains("Living Room TV"));
    }

    @Test
    public void testIsOnline() {
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        Assertions.assertFalse(tv.isOnline());
        tv.togglePower();
        Assertions.assertTrue(tv.isOnline());
    }
}
