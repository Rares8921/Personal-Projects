package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for FileCategories class.
 */
class FileCategoriesTest {

    @Test
    @DisplayName("categorize identifies document extensions")
    void testDocuments() {
        assertEquals("Documents", FileCategories.categorize(".pdf"));
        assertEquals("Documents", FileCategories.categorize(".docx"));
        assertEquals("Documents", FileCategories.categorize(".txt"));
        assertEquals("Documents", FileCategories.categorize(".xlsx"));
    }

    @Test
    @DisplayName("categorize identifies image extensions")
    void testImages() {
        assertEquals("Images", FileCategories.categorize(".jpg"));
        assertEquals("Images", FileCategories.categorize(".png"));
        assertEquals("Images", FileCategories.categorize(".svg"));
        assertEquals("Images", FileCategories.categorize(".webp"));
    }

    @Test
    @DisplayName("categorize identifies video extensions")
    void testVideo() {
        assertEquals("Video", FileCategories.categorize(".mp4"));
        assertEquals("Video", FileCategories.categorize(".mkv"));
        assertEquals("Video", FileCategories.categorize(".avi"));
    }

    @Test
    @DisplayName("categorize identifies audio extensions")
    void testAudio() {
        assertEquals("Audio", FileCategories.categorize(".mp3"));
        assertEquals("Audio", FileCategories.categorize(".flac"));
        assertEquals("Audio", FileCategories.categorize(".wav"));
    }

    @Test
    @DisplayName("categorize identifies archive extensions")
    void testArchives() {
        assertEquals("Archives", FileCategories.categorize(".zip"));
        assertEquals("Archives", FileCategories.categorize(".rar"));
        assertEquals("Archives", FileCategories.categorize(".7z"));
    }

    @Test
    @DisplayName("categorize identifies code extensions")
    void testCode() {
        assertEquals("Code", FileCategories.categorize(".java"));
        assertEquals("Code", FileCategories.categorize(".py"));
        assertEquals("Code", FileCategories.categorize(".js"));
        assertEquals("Code", FileCategories.categorize(".c"));
    }

    @Test
    @DisplayName("categorize identifies executables")
    void testExecutables() {
        assertEquals("Executables", FileCategories.categorize(".exe"));
        assertEquals("Executables", FileCategories.categorize(".dll"));
        assertEquals("Executables", FileCategories.categorize(".msi"));
    }

    @Test
    @DisplayName("categorize returns Other for unknown extensions")
    void testOther() {
        assertEquals("Other", FileCategories.categorize(".xyz123"));
        assertEquals("Other", FileCategories.categorize(".unknownformat"));
    }

    @Test
    @DisplayName("categorize is case-insensitive")
    void testCaseInsensitive() {
        assertEquals("Documents", FileCategories.categorize(".PDF"));
        assertEquals("Images", FileCategories.categorize(".JPG"));
        assertEquals("Video", FileCategories.categorize(".MP4"));
    }

    @Test
    @DisplayName("categorize handles null and empty input")
    void testNullEmpty() {
        assertEquals("Other", FileCategories.categorize((String) null));
        assertEquals("Other", FileCategories.categorize(""));
    }

    @Test
    @DisplayName("categorize FileEntry returns category based on extension")
    void testCategorizeFileEntry() {
        FileEntry file = new FileEntry("photo.jpg", 5000, false, 0);
        assertEquals("Images", FileCategories.categorize(file));
    }

    @Test
    @DisplayName("categorize FileEntry returns Directories for dirs")
    void testCategorizeDirectory() {
        FileEntry dir = new FileEntry("C:\\folder", 0, true, 0);
        assertEquals("Directories", FileCategories.categorize(dir));
    }

    @Test
    @DisplayName("groupByCategory groups file entries correctly")
    void testGroupByCategory() {
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("a.jpg", 1000, false, 0),
            new FileEntry("b.jpg", 2000, false, 0),
            new FileEntry("c.pdf", 3000, false, 0),
            new FileEntry("d.mp4", 50000, false, 0)
        );
        Map<String, long[]> groups = FileCategories.groupByCategory(entries);
        assertNotNull(groups.get("Images"));
        assertEquals(3000, groups.get("Images")[0]);  // total size
        assertEquals(2, groups.get("Images")[1]);      // count
        assertEquals(3000, groups.get("Documents")[0]);
        assertEquals(50000, groups.get("Video")[0]);
    }

    @Test
    @DisplayName("getAllCategories returns 12 built-in categories")
    void testGetAllCategories() {
        List<String> cats = FileCategories.getAllCategories();
        assertEquals(12, cats.size());
        assertTrue(cats.contains("Documents"));
        assertTrue(cats.contains("Images"));
        assertTrue(cats.contains("Other"));
    }
}
