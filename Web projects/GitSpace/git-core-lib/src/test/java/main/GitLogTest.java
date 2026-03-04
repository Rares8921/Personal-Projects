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

class GitLogTest {

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
    void testLogTraversal(@TempDir Path tempDir) throws Exception {
        String repoPath = tempDir.toAbsolutePath().toString();
        Main.main(new String[]{"init", repoPath});
        getAndClearOutput();

        Files.writeString(tempDir.resolve("a.txt"), "A");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "a.txt"});
        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t1 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t1, "-m", "First Commit"});
        String c1 = getAndClearOutput();

        Files.writeString(tempDir.resolve("b.txt"), "B");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "b.txt"});
        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t2 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t2, "-p", c1, "-m", "Second Commit"});
        String c2 = getAndClearOutput();

        Files.writeString(tempDir.resolve("c.txt"), "C");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "c.txt"});
        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t3 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t3, "-p", c2, "-m", "Third Commit"});
        String c3 = getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "log"});
        String logOutput = getAndClearOutput();

        System.err.println("logOutput:\n" + logOutput);

        assertTrue(logOutput.contains("Third Commit"), "Log should contain latest commit");
        assertTrue(logOutput.contains("Second Commit"), "Log should contain second commit");
        assertTrue(logOutput.contains("First Commit"), "Log should contain first commit");

        assertTrue(logOutput.contains(c3), "Log should show hash of c3");
        assertTrue(logOutput.contains(c1), "Log should show hash of c1");

        assertTrue(logOutput.indexOf("Third Commit") < logOutput.indexOf("First Commit"),
                "Log should be in reverse chronological order");
    }
}