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

class GitMergeTest {

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
    void testRealMergeFlow(@TempDir Path tempDir) throws Exception {
        String repoPath = tempDir.toAbsolutePath().toString();
        Main.main(new String[]{"init", repoPath});
        getAndClearOutput();

        Files.writeString(tempDir.resolve("base.txt"), "Base");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "base.txt"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t1 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t1, "-m", "Base"});
        String c1 = getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "branch", "feature"});
        getAndClearOutput();

        Files.writeString(tempDir.resolve("master_only.txt"), "Master stuff");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "master_only.txt"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t2 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t2, "-p", c1, "-m", "Master Work"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "checkout", "feature"});
        getAndClearOutput();

        Files.writeString(tempDir.resolve("feature_only.txt"), "Feature stuff");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "feature_only.txt"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String t3 = getAndClearOutput();
        Main.main(new String[]{"-C", repoPath, "commit-tree", t3, "-p", c1, "-m", "Feature Work"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "checkout", "master"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "merge", "feature"});
        String output = getAndClearOutput();

        assertTrue(Files.exists(tempDir.resolve("base.txt")), "Base file missing");
        assertTrue(Files.exists(tempDir.resolve("master_only.txt")), "Master file missing");
        assertTrue(Files.exists(tempDir.resolve("feature_only.txt")), "Feature file missing (Merge failed)");

        assertTrue(output.matches("[a-f0-9]{40}"), "Merge should output new commit hash");
    }
}