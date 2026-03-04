package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitDiffTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private String getAndClearOutput() {
        String output = outContent.toString().trim();
        outContent.reset();
        return output;
    }

    @Test
    void testDiffBetweenCommits(@TempDir Path tempDir) throws Exception {
        String repoPath = tempDir.toAbsolutePath().toString();

        Main.main(new String[]{"init", repoPath});
        getAndClearOutput();

        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "Version 1");

        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "file.txt"});
        getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t1 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t1, "-m", "Base"});
        String c1 = getAndClearOutput();

        Files.writeString(file, "Version 2"); // Modified
        Files.writeString(tempDir.resolve("new.txt"), "New File");

        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "file.txt"});
        getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "new.txt"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t2 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t2, "-p", c1, "-m", "Changes"});
        String c2 = getAndClearOutput();

        Files.delete(tempDir.resolve("new.txt"));

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t3 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t3, "-p", c2, "-m", "Deletion"});
        String c3 = getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "diff", c1, c2});
        String diff1 = getAndClearOutput();

        System.err.println("Diff 1 Output:\n" + diff1);

        assertTrue(diff1.contains("M\tfile.txt"), "Should show file.txt as Modified (M)");
        assertTrue(diff1.contains("A\tnew.txt"), "Should show new.txt as Added (A)");

        Main.main(new String[]{"-C", repoPath, "diff", c2, c3});
        String diff2 = getAndClearOutput();

        System.err.println("Diff 2 Output:\n" + diff2);

        assertTrue(diff2.contains("D\tnew.txt"), "Should show new.txt as Deleted (D)");
        assertFalse(diff2.contains("file.txt"), "Unchanged files should not be listed");
    }
}