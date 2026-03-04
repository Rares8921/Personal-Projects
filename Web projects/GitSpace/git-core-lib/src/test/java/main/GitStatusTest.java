package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GitStatusTest {

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
    void testStatus(@TempDir Path tempDir) throws Exception {
        String repoPath = tempDir.toAbsolutePath().toString();

        Main.main(new String[]{"init", repoPath});
        getAndClearOutput();

        Files.writeString(tempDir.resolve("clean.txt"), "Clean Content");
        Files.writeString(tempDir.resolve("modify.txt"), "Original Content");
        Files.writeString(tempDir.resolve("delete.txt"), "To be deleted");

        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "clean.txt"});
        getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "modify.txt"});
        getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "delete.txt"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String treeHash = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", treeHash, "-m", "Initial Commit"});
        getAndClearOutput();

        Files.writeString(tempDir.resolve("modify.txt"), "Modified Content");

        Files.delete(tempDir.resolve("delete.txt"));

        Files.writeString(tempDir.resolve("new.txt"), "Untracked Content");

        Main.main(new String[]{"-C", repoPath, "status"});
        String statusOutput = getAndClearOutput();

        System.err.println("Status Output:\n" + statusOutput);

        assertTrue(statusOutput.contains("M\tmodify.txt"), "Should detect modified file");
        assertTrue(statusOutput.contains("D\tdelete.txt"), "Should detect deleted file");
        assertTrue(statusOutput.contains("A\tnew.txt"), "Should detect untracked/new file"); // 'A' = Added/Untracked

        assertFalse(statusOutput.contains("clean.txt"), "Clean files should not be listed");
    }
}