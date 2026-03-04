package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitBranchingTest {

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

    /**
     * 1. Commit pe Master (v1)
     * 2. Creare branch Feature
     * 3. Switch pe Feature -> Modificare fisier -> Commit (v2)
     * 4. Switch pe Master -> fisierul revine la v1
     * 5. Switch pe Feature -> fisierul revine la v2
     */
    @Test
    void testBranchingAndCheckout(@TempDir Path tempDir) throws Exception {
        String repoPath = tempDir.toAbsolutePath().toString();

        // 1. INIT
        Main.main(new String[]{"init", repoPath});
        assertTrue(Files.exists(tempDir.resolve(".git")), "Repo not initialized");
        getAndClearOutput();

        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "Versiunea 1");

        // Hash Object
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "test.txt"});
        getAndClearOutput();

        // Write Tree
        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String treeHashV1 = getAndClearOutput();

        // Commit pe Master
        Main.main(new String[]{"-C", repoPath, "commit-tree", treeHashV1, "-m", "Commit 1"});
        String commitV1 = getAndClearOutput();
        String masterRefContent = Files.readString(tempDir.resolve(".git/refs/heads/master")).trim();
        assertEquals(commitV1, masterRefContent, "Master ref should point to Commit 1");


        Main.main(new String[]{"-C", repoPath, "branch", "feature"});
        getAndClearOutput();

        Path featureRef = tempDir.resolve(".git/refs/heads/feature");
        assertTrue(Files.exists(featureRef), "Branch ref file should be created");
        assertEquals(commitV1, Files.readString(featureRef).trim(), "Feature branch should point to Commit 1 initially");

        Main.main(new String[]{"-C", repoPath, "checkout", "feature"});
        getAndClearOutput();

        String headContent = Files.readString(tempDir.resolve(".git/HEAD")).trim();
        assertEquals("ref: refs/heads/feature", headContent, "HEAD should point to feature branch");

        Files.writeString(testFile, "Versiunea 2 (Feature)");

        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "test.txt"});
        getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String treeHashV2 = getAndClearOutput();

        Main.main(new String[]{"-C", repoPath, "commit-tree", treeHashV2, "-p", commitV1, "-m", "Commit 2 on Feature"});
        String commitV2 = getAndClearOutput();

        String featureRefContent = Files.readString(featureRef).trim();
        masterRefContent = Files.readString(tempDir.resolve(".git/refs/heads/master")).trim();

        assertEquals(commitV2, featureRefContent, "Feature ref should update to Commit 2");
        assertEquals(commitV1, masterRefContent, "Master ref should stay at Commit 1");

        Main.main(new String[]{"-C", repoPath, "checkout", "master"});
        getAndClearOutput();

        headContent = Files.readString(tempDir.resolve(".git/HEAD")).trim();
        assertEquals("ref: refs/heads/master", headContent);

        String contentOnDisk = Files.readString(testFile);
        assertEquals("Versiunea 1", contentOnDisk, "Checkout master failed to restore Version 1");


        Main.main(new String[]{"-C", repoPath, "checkout", "feature"});
        getAndClearOutput();

        contentOnDisk = Files.readString(testFile);
        assertEquals("Versiunea 2 (Feature)", contentOnDisk, "Checkout feature failed to restore Version 2");
    }
}