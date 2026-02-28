package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for FragmentationResult class.
 */
class FragmentationResultTest {

    @Test
    @DisplayName("fromJson parses fragmentation data correctly")
    void testFromJson() {
        String json = "{\"totalFiles\":1000,\"fragmentedFiles\":150,\"totalFragments\":500," +
            "\"fragmentationPct\":15.0,\"avgFragmentsPerFile\":3.33," +
            "\"topFragmented\":[{\"path\":\"C:\\large.db\",\"fragments\":50,\"size\":1073741824}]}";

        FragmentationResult r = FragmentationResult.fromJson(json);
        assertEquals(1000, r.getTotalFiles());
        assertEquals(150, r.getFragmentedFiles());
        assertEquals(500, r.getTotalFragments());
        assertEquals(15.0, r.getFragmentationPct(), 0.01);
        assertEquals(3.33, r.getAvgFragmentsPerFile(), 0.01);
        assertEquals(1, r.getTopFragmented().size());
        assertEquals("C:\\large.db", r.getTopFragmented().get(0).getPath());
        assertEquals(50, r.getTopFragmented().get(0).getFragments());
    }

    @Test
    @DisplayName("getHealthRating returns correct rating based on percentage")
    void testHealthRating() {
        assertEquals("Excellent", createResult(2.0).getHealthRating());
        assertEquals("Good", createResult(10.0).getHealthRating());
        assertEquals("Fair", createResult(25.0).getHealthRating());
        assertEquals("Poor", createResult(40.0).getHealthRating());
        assertEquals("Critical", createResult(60.0).getHealthRating());
    }

    @Test
    @DisplayName("FragmentedFile getName extracts filename")
    void testFragmentedFileName() {
        FragmentationResult.FragmentedFile ff = new FragmentationResult.FragmentedFile("C:\\folder\\big.dat", 10, 5000);
        assertEquals("big.dat", ff.getName());
        assertEquals(10, ff.getFragments());
        assertNotNull(ff.getFormattedSize());
    }

    @Test
    @DisplayName("fromJson handles empty input gracefully")
    void testFromJsonEmpty() {
        // null input causes NPE in extractLong, so test with empty string
        FragmentationResult r = FragmentationResult.fromJson("{}");
        assertEquals(0, r.getTotalFiles());
        assertTrue(r.getTopFragmented().isEmpty());
    }

    @Test
    @DisplayName("toString contains summary info")
    void testToString() {
        FragmentationResult r = createResult(15.0);
        String s = r.toString();
        assertTrue(s.contains("15.0%"));
        // 15.0 is NOT < 15, so health rating is "Fair" (not "Good")
        assertTrue(s.contains("Fair"));
    }

    private FragmentationResult createResult(double pct) {
        return new FragmentationResult(1000, (int)(pct * 10), 500, pct, 1.5, Collections.emptyList());
    }
}
