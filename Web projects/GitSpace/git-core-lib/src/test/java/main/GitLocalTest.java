package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitLocalTest {

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

    private String getOutput() {
        String s = outContent.toString().trim();
        outContent.reset();
        return s;
    }

    @Test
    void testFullLocalLifecycle(@TempDir Path tempDir) throws Exception {
        String repo = tempDir.toAbsolutePath().toString();

        Main.main(new String[]{"init", repo});
        assertTrue(Files.exists(tempDir.resolve(".git")));
        getOutput();

        Files.writeString(tempDir.resolve("file.txt"), "v1");
        Main.main(new String[]{"-C", repo, "hash-object", "-w", "file.txt"});
        getOutput();

        Main.main(new String[]{"-C", repo, "write-tree"});
        String tree1 = getOutput();
        assertTrue(tree1.matches("[a-f0-9]{40}"));

        Main.main(new String[]{"-C", repo, "commit-tree", tree1, "-m", "Init"});
        String commit1 = getOutput();
        assertTrue(commit1.matches("[a-f0-9]{40}"));

        Main.main(new String[]{"-C", repo, "branch", "feature"});
        assertTrue(getOutput().contains("Created branch 'feature'"));

        Main.main(new String[]{"-C", repo, "checkout", "feature"});
        assertTrue(getOutput().contains("Switched to branch 'feature'"));

        Files.writeString(tempDir.resolve("file.txt"), "v2");
        Main.main(new String[]{"-C", repo, "hash-object", "-w", "file.txt"});
        getOutput();
        Main.main(new String[]{"-C", repo, "write-tree"});
        String tree2 = getOutput();
        Main.main(new String[]{"-C", repo, "commit-tree", tree2, "-p", commit1, "-m", "FeatureCommit"});
        String commit2 = getOutput();

        Main.main(new String[]{"-C", repo, "log"});
        String log = getOutput();
        assertTrue(log.contains(commit2));
        assertTrue(log.contains(commit1));

        Files.writeString(tempDir.resolve("new.txt"), "untracked");
        Main.main(new String[]{"-C", repo, "status"});
        String status = getOutput();
        assertTrue(status.contains("A\tnew.txt"));

        Main.main(new String[]{"-C", repo, "checkout", "master"});
        getOutput();
        Main.main(new String[]{"-C", repo, "merge", "feature"});
        String mergeOutput = getOutput();
        assertTrue(mergeOutput.matches("[a-f0-9]{40}") || mergeOutput.equals(commit2)); // Fast-forward or merge commit

        assertEquals("v2", Files.readString(tempDir.resolve("file.txt")));
    }

    @Test
    void testCatFileAndDebug(@TempDir Path tempDir) throws Exception {
        String repo = tempDir.toAbsolutePath().toString();
        Main.main(new String[]{"init", repo});
        getOutput();

        Files.writeString(tempDir.resolve("x.txt"), "content");
        Main.main(new String[]{"-C", repo, "hash-object", "-w", "x.txt"});
        String hash = getOutput();

        Main.main(new String[]{"-C", repo, "cat-file", "-p", hash});
        assertEquals("content", getOutput());

        Main.main(new String[]{"-C", repo, "debug-object", hash});
        assertTrue(getOutput().contains("Decoding object"));
    }
}