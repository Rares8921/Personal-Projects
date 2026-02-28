package com.example.ihas;

import com.example.ihas.devices.SmartCamera;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartCameraTest {

    @Test
    public void testToggleRecording() {
        SmartCamera camera = new SmartCamera("C001", "Front Door Camera", "1080p");
        boolean initialState = camera.isRecording();
        camera.togglePower();
        Assertions.assertNotEquals(initialState, camera.isRecording(), "Toggle should change recording state");
    }

    @Test
    public void testSetDetectionSensitivityValid() {
        SmartCamera camera = new SmartCamera("C001", "Front Door Camera", "1080p");
        camera.setDetectionSensitivity(8);
        Assertions.assertEquals(8, camera.getDetectionSensitivity());
    }

    @Test
    public void testSetDetectionSensitivityInvalid() {
        SmartCamera camera = new SmartCamera("C001", "Front Door Camera", "1080p");
        Assertions.assertThrows(IllegalArgumentException.class, () -> camera.setDetectionSensitivity(0));
    }
}
