package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for NetworkDriveUtil class.
 */
class NetworkDriveUtilTest {

    @Test
    @DisplayName("isUncPath correctly identifies UNC paths")
    void testIsUncPath() {
        assertTrue(NetworkDriveUtil.isUncPath("\\\\server\\share"));
        assertTrue(NetworkDriveUtil.isUncPath("\\\\192.168.1.1\\data"));
        assertFalse(NetworkDriveUtil.isUncPath("C:\\folder"));
        assertFalse(NetworkDriveUtil.isUncPath("\\single\\backslash"));
        assertFalse(NetworkDriveUtil.isUncPath(null));
        assertFalse(NetworkDriveUtil.isUncPath(""));
    }

    @Test
    @DisplayName("validateUncPath returns false for invalid paths")
    void testValidateUncPathInvalid() {
        assertFalse(NetworkDriveUtil.validateUncPath("C:\\local"));
        assertFalse(NetworkDriveUtil.validateUncPath(null));
        assertFalse(NetworkDriveUtil.validateUncPath(""));
    }

    @Test
    @DisplayName("getDisplayName formats UNC paths with server name")
    void testGetDisplayNameUNC() {
        String display = NetworkDriveUtil.getDisplayName("\\\\fileserver\\shared");
        assertEquals("Network: \\\\fileserver", display);
    }

    @Test
    @DisplayName("getDisplayName returns path as-is for local paths")
    void testGetDisplayNameLocal() {
        assertEquals("C:\\Users", NetworkDriveUtil.getDisplayName("C:\\Users"));
    }

    @Test
    @DisplayName("detectNetworkDrives returns a list (may be empty)")
    void testDetectNetworkDrives() {
        List<NetworkDriveUtil.NetworkDrive> drives = NetworkDriveUtil.detectNetworkDrives();
        assertNotNull(drives);
        // Can't assert non-empty since test machine may not have mapped drives
    }

    @Test
    @DisplayName("NetworkDrive class stores fields correctly")
    void testNetworkDriveClass() {
        NetworkDriveUtil.NetworkDrive nd = new NetworkDriveUtil.NetworkDrive(
            "Z:", "\\\\server\\share", "OK", 1000000000L, 500000000L);

        assertEquals("Z:", nd.getLocalPath());
        assertEquals("\\\\server\\share", nd.getUncPath());
        assertEquals("OK", nd.getStatus());
        assertEquals(1000000000L, nd.getTotalBytes());
        assertEquals(500000000L, nd.getFreeBytes());
        assertEquals(500000000L, nd.getUsedBytes());
        assertTrue(nd.isAvailable());
    }

    @Test
    @DisplayName("NetworkDrive isAvailable returns false for disconnected")
    void testNetworkDriveDisconnected() {
        NetworkDriveUtil.NetworkDrive nd = new NetworkDriveUtil.NetworkDrive(
            "Z:", "\\\\server\\share", "Disconnected", 0, 0);
        assertFalse(nd.isAvailable());
    }
}
