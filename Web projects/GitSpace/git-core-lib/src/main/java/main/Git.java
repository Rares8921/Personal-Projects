package main;

import main.domain.Blob;
import main.domain.Commit;
import main.domain.GitObject;
import main.domain.ObjectType;
import main.domain.Tree;
import main.domain.tree.TreeEntry;
import main.domain.tree.TreeEntryMode;
import main.domain.tree.TreeEntryModeType;
import main.pack.DeltaInstruction;
import main.pack.PackObject;
import main.pack.PackParser;
import main.protocol.HttpGitTransport;
import main.util.ChangeType;
import main.util.Platform;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Core entry point for the Git version control system implementation.
 * Acts as a facade for repository management, object storage (blob/tree/commit),
 * and high-level commands like clone, init, branching, and checkout.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Git {

    private static final String DIR_GIT = ".git";
    private static final String DIR_OBJECTS = "objects";
    private static final String DIR_REFS = "refs";
    private static final String FILE_HEAD = "HEAD";
    private static final String FILE_CONFIG = "config";
    private static final String REF_MASTER = "ref: refs/heads/master\n";
    private static final String CONFIG_DEFAULT = "[core]\n\tautocrlf = false";

    private static final Set<String> IGNORED_DIRS = Set.of(DIR_GIT);
    private static final String HASH_ALGORITHM = "SHA-1";

    private final Path rootDirectory;

    /**
     * Initializes a new Git repository at the specified path.
     * Creates the necessary .git directory structure and default configuration.
     *
     * @param root The root directory where the repository should be initialized.
     * @return An instance of the initialized Git repository.
     * @throws IOException If filesystem operations fail or repository already exists.
     */
    public static Git init(Path root) throws IOException {
        final var git = new Git(root);
        final var dotGit = git.resolveDotGit();

        if (Files.exists(dotGit)) {
            throw new FileAlreadyExistsException("Repository already initialized at " + dotGit);
        }

        Files.createDirectories(git.resolveObjectsDir());
        Files.createDirectories(git.resolveRefsDir().resolve("heads"));

        writeStringToFile(git.resolveHeadFile(), REF_MASTER);
        writeStringToFile(git.resolveConfigFile(), CONFIG_DEFAULT);

        return git;
    }

    /**
     * Opens an existing Git repository.
     * Validates that the .git directory exists before returning the instance.
     *
     * @param root The root directory of the existing repository.
     * @return An instance of the Git repository.
     * @throws IOException If the repository does not exist or cannot be accessed.
     */
    public static Git open(Path root) throws IOException {
        final var git = new Git(root);
        if (!Files.exists(git.resolveDotGit())) {
            throw new NoSuchFileException("Not a git repository: " + root);
        }
        return git;
    }

    /**
     * Clones a remote repository from a URI to a local path.
     * Performs a fetch of the packfile, expands objects, resolves deltas, and checks out HEAD.
     *
     * @param uri The remote URI to clone from.
     * @param destination The local path to clone into.
     * @param token The authentication token
     * @return An instance of the cloned Git repository.
     * @throws IOException If network or IO operations fail.
     * @throws DataFormatException If the packfile is corrupt.
     * @throws NoSuchAlgorithmException If the hashing algorithm is not supported.
     */
    public static Git clone(URI uri, Path destination, String token) throws IOException, DataFormatException, NoSuchAlgorithmException {
        final var client = new HttpGitTransport(uri, token);
        final var references = client.fetchReferences();

        if (references.isEmpty()) {
            throw new IOException("Remote repository is empty or unreachable.");
        }

        final var headRef = references.getFirst();
        final var packBytes = client.getPack(headRef);

        final var git = init(destination);
        git.unpackAndApply(packBytes);

        final var headCommit = git.readCommit(headRef.hash());
        git.checkout(git.readTree(headCommit.treeHash()));

        Path masterRef = git.resolveRefsDir().resolve("heads/master");
        writeStringToFile(masterRef, headRef.hash());

        return git;
    }

    // Adauga asta in clasa Git (de exemplu sub metoda push)

    /**
     * Pulls changes from a remote repository and merges them into the current branch.
     * Steps:
     * 1. Connect to remote and fetch references.
     * 2. Find the target branch hash.
     * 3. Download the Packfile and unpack objects (Fetch).
     * 4. Create a temporary local ref for the fetched commit.
     * 5. Call merge() with that temporary ref.
     */
    public void pull(String remoteUrl, String branchName, String token) throws Exception {
        URI uri = URI.create(remoteUrl);

        try (var client = new HttpGitTransport(uri, token)) {
            var references = client.fetchReferences();

            if (references.isEmpty()) {
                throw new IOException("Remote repository is empty.");
            }

            var remoteRef = references.stream()
                    .filter(r -> r.name().endsWith("/" + branchName) || r.name().equals(branchName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Remote branch not found: " + branchName));

            byte[] packBytes = client.getPack(remoteRef);

            unpackAndApply(packBytes);

            String trackingBranchName = "origin_" + branchName;
            Path trackingRefPath = resolveRefsDir().resolve("heads").resolve(trackingBranchName);

            writeStringToFile(trackingRefPath, remoteRef.hash());

            System.out.println("Fetch complete. Merging " + remoteRef.hash() + " into current HEAD...");

            merge(trackingBranchName);

        }
    }

    /**
     * Reads and deserializes a specific Git object (Commit, Tree, Blob) by its hash.
     *
     * @param type The class type of the object to read.
     * @param hash The SHA-1 hash of the object.
     * @return The deserialized object.
     * @throws IOException If the object cannot be found or read.
     */
    public <T extends GitObject> T readObject(ObjectType<T> type, String hash) throws IOException {
        final var raw = readRawObject(hash);

        if (!raw.type.getName().equals(type.getName())) {
            throw new IllegalArgumentException(
                    "Type mismatch: expected " + type.getName() + " but found " + raw.type.getName() + " for hash " + hash
            );
        }

        try (var stream = new DataInputStream(new ByteArrayInputStream(raw.content))) {
            return type.deserialize(stream);
        }
    }

    public Blob readBlob(String hash) throws IOException {
        return readObject(ObjectType.BLOB, hash);
    }

    public Commit readCommit(String hash) throws IOException {
        return readObject(ObjectType.COMMIT, hash);
    }

    public Tree readTree(String hash) throws IOException {
        return readObject(ObjectType.TREE, hash);
    }

    /**
     * Writes a high-level Git object to the object store.
     * Handles serialization, hashing, and compression.
     *
     * @param object The domain object to write.
     * @return The SHA-1 hash of the written object.
     * @throws IOException If writing fails.
     * @throws NoSuchAlgorithmException If SHA-1 is unavailable.
     */
    @SuppressWarnings("unchecked")
    public String writeObject(GitObject object) throws IOException, NoSuchAlgorithmException {
        final var type = ObjectType.byClass(object.getClass());
        final var serializedContent = type.serializeContent(object);
        return writeRawObject(new RawObject(type, serializedContent));
    }

    /**
     * Recursively writes a directory structure as Tree and Blob objects.
     *
     * @param directory The directory path to snapshot.
     * @return The hash of the root Tree object.
     * @throws IOException If file access fails.
     * @throws NoSuchAlgorithmException If SHA-1 is unavailable.
     */
    public String writeTree(Path directory) throws IOException, NoSuchAlgorithmException {
        final var entries = new ArrayList<TreeEntry>();

        try (var stream = Files.list(directory)) {
            final var children = stream
                    .filter(p -> !IGNORED_DIRS.contains(p.getFileName().toString()))
                    .toList();

            for (var child : children) {
                entries.add(processTreeEntry(child));
            }
        }

        entries.sort(Comparator.naturalOrder());
        return writeObject(new Tree(entries));
    }

    /**
     * Restores the working directory to the state described by the given Tree.
     * This is a hard reset: existing files are overwritten.
     *
     * @param tree The tree structure to restore.
     * @throws IOException If filesystem operations fail.
     */
    public void checkout(Tree tree) throws IOException {
        checkoutRecursive(tree, rootDirectory);
    }


    /**
     * Creates a new branch reference pointing to the current HEAD commit.
     * <p>
     * This operation only creates the reference file in {@code .git/refs/heads/}.
     * It does not switch the active branch (HEAD remains unchanged).
     *
     * @param branchName The name of the new branch (e.g., "feature-login").
     * @throws IOException If the HEAD is invalid (empty repo) or the branch already exists.
     */
    public void createBranch(String branchName) throws IOException {
        String currentHash = resolveHead();
        if (currentHash == null) {
            throw new IOException("Cannot create branch: HEAD does not point to a valid commit (repo empty?)");
        }

        Path branchFile = resolveRefsDir().resolve("heads").resolve(branchName);
        if (Files.exists(branchFile)) {
            throw new FileAlreadyExistsException("Branch already exists: " + branchName);
        }

        writeStringToFile(branchFile, currentHash);
    }



    /**
     * Switches the active branch and updates the working directory.
     * <p>
     * This method performs three main actions:
     * 1. Resolves the target commit hash from the branch reference.
     * 2. Restores the working directory files to match that commit.
     * 3. Updates {@code .git/HEAD} to point to the new branch ref.
     *
     * @param branchName The name of the branch to switch to.
     * @throws IOException If the branch does not exist or file restoration fails.
     */
    public void checkout(String branchName) throws IOException {
        Path branchFile = resolveRefsDir().resolve("heads").resolve(branchName);
        if (!Files.exists(branchFile)) {
            throw new NoSuchFileException("Branch not found: " + branchName);
        }

        String targetCommitHash = Files.readString(branchFile).trim();

        Commit commit = readCommit(targetCommitHash);
        Tree tree = readTree(commit.treeHash());

        checkout(tree);

        writeStringToFile(resolveHeadFile(), "ref: refs/heads/" + branchName + "\n");
    }

    /**
     * Resolves the current HEAD to a concrete SHA-1 hash.
     * Handles both detached HEAD (direct hash) and symbolic refs (ref: refs/heads/...).
     *
     * @return The SHA-1 hash of the current commit, or null if the repository is empty/corrupt.
     * @throws IOException If reading the HEAD file fails.
     */
    public String resolveHead() throws IOException {
        if (!Files.exists(resolveHeadFile())) return null;

        String content = Files.readString(resolveHeadFile()).trim();
        if (content.startsWith("ref: ")) {
            String refPath = content.substring(5);
            Path refFile = resolveDotGit().resolve(refPath);
            if (Files.exists(refFile)) {
                return Files.readString(refFile).trim();
            }
            return null;
        }
        return content;
    }

    /**
     * Resolves a branch name to its commit hash.
     * e.g., "master" -> "a1b2c3..."
     * * @param branchName The short name of the branch.
     * @return The commit hash, or null if the branch does not exist.
     */
    public String resolveBranch(String name) throws IOException {
        Path branchRef = resolveRefsDir().resolve("heads").resolve(name);
        if (Files.exists(branchRef)) {
            return Files.readString(branchRef).trim();
        }

        Path tagRef = resolveRefsDir().resolve("tags").resolve(name);
        if (Files.exists(tagRef)) {
            return Files.readString(tagRef).trim();
        }

        return null;
    }

    /**
     * Pushes the current branch's changes to a remote repository.
     * <p>
     * This process involves:
     * Resolving the current HEAD commit hash.
     * Generating a compressed Packfile containing all necessary objects (BFS traversal).
     * Transmitting the Packfile via the appropriate Transport protocol (HTTP/SSH).
     *
     * @param remoteUrl  The URL of the remote repository.
     * @param branchName The name of the branch to push (e.g., "master").
     * @param token The JWT token
     */
    public void push(String remoteUrl, String branchName, String token) throws IOException, NoSuchAlgorithmException {
        String branchHash = resolveHead();
        if (branchHash == null) throw new IOException("Cannot push: HEAD invalid");

        main.pack.PackGenerator generator = new main.pack.PackGenerator(this);
        byte[] packData = generator.createPack(branchHash);

        URI uri = URI.create(remoteUrl);
        try (var transport = new HttpGitTransport(uri, token)) {
            transport.push(remoteUrl, branchHash, branchName, packData);
        }
    }

    /**
     * Retrieves the commit history starting from the current HEAD.
     * Performs a graph traversal (BFS) to find all accessible commits.
     *
     * @return A list of LogEntry objects representing the history.
     * @throws IOException If the object store cannot be read.
     */
    public java.util.List<LogEntry> log() throws IOException {
        String currentHash = resolveHead();
        if (currentHash == null) {
            return java.util.List.of(); // Repository gol
        }

        var history = new java.util.ArrayList<LogEntry>();
        var visited = new java.util.HashSet<String>();
        var queue = new java.util.ArrayDeque<String>();

        queue.add(currentHash);

        while (!queue.isEmpty()) {
            String hash = queue.poll();

            if (visited.contains(hash)) continue;
            visited.add(hash);

            try {
                Commit commit = readCommit(hash);
                history.add(new LogEntry(hash, commit));

                queue.addAll(commit.parents());
            } catch (Exception e) {
                System.err.println("Warning: Could not read commit " + hash);
            }
        }

        return history;
    }

    /**
     * Checks if a merge can be performed without conflicts.
     * Does NOT change the working directory or write commits.
     */
    public boolean canMerge(String targetBranch, String sourceBranch) throws IOException {
        String targetHash = resolveBranch(targetBranch);
        String sourceHash = resolveBranch(sourceBranch);

        if (targetHash == null || sourceHash == null) {
            return false; // Branches not found
        }

        if (targetHash.equals(sourceHash)) return true;

        String baseHash = findMergeBase(targetHash, sourceHash);
        if (baseHash == null) return false;

        try {
            Commit targetCommit = readCommit(targetHash);
            Commit sourceCommit = readCommit(sourceHash);
            Commit baseCommit = readCommit(baseHash);

            mergeTrees(baseCommit.treeHash(), targetCommit.treeHash(), sourceCommit.treeHash());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Merges the specified branch into the current HEAD.
     * <p>
     * Strategy:
     * Resolve O (Ours/HEAD) and T (Theirs/Target Branch).
     * Find B (Merge Base) - the common ancestor.
     * Perform a 3-way merge on the Trees of B, O, and T.
     * Create a new Merge Commit with parents [O, T].
     * Update HEAD and Working Directory.
     *
     * @param branchName The branch to merge into the current one.
     * @return The hash of the new merge commit.
     */
    public String merge(String branchName) throws IOException, NoSuchAlgorithmException {
        String oursHash = resolveHead();

        Path branchRef = resolveRefsDir().resolve("heads").resolve(branchName);
        if (!Files.exists(branchRef)) {
            throw new IllegalArgumentException("Branch not found: " + branchName);
        }
        String theirsHash = Files.readString(branchRef).trim();

        if (oursHash.equals(theirsHash)) {
            return oursHash;
        }

        Commit ours = readCommit(oursHash);
        Commit theirs = readCommit(theirsHash);

        String baseHash = findMergeBase(oursHash, theirsHash);
        if (baseHash == null) {
            throw new IllegalStateException("No common ancestor found (unrelated histories?)");
        }

        Commit base = readCommit(baseHash);

        String mergedTreeHash = mergeTrees(base.treeHash(), ours.treeHash(), theirs.treeHash());

        String message = "Merge branch '" + branchName + "'";
        java.util.List<String> parents = java.util.List.of(oursHash, theirsHash);

        main.domain.AuthorSignature author = main.domain.AuthorSignature.now("User", "user@example.com");
        Commit mergeCommit = new Commit(mergedTreeHash, parents, author, author, message);
        String mergeCommitHash = writeObject(mergeCommit);

        // update HEAD & working dir
        checkout(readTree(mergedTreeHash));

        // move current branch pointer
        String headContent = Files.readString(resolveHeadFile()).trim();
        if (headContent.startsWith("ref: ")) {
            Path refFile = resolveDotGit().resolve(headContent.substring(5));
            writeStringToFile(refFile, mergeCommitHash);
        } else {
            writeStringToFile(resolveHeadFile(), mergeCommitHash); // Detached HEAD update
        }

        return mergeCommitHash;
    }

    /**
     * Finds the best common ancestor between two commits.
     */
    public String findMergeBase(String commitA, String commitB) throws IOException {
        Set<String> ancestorsA = new java.util.HashSet<>();
        var queue = new java.util.ArrayDeque<String>();
        queue.add(commitA);

        while (!queue.isEmpty()) {
            String c = queue.poll();
            if (ancestorsA.contains(c)) continue;
            ancestorsA.add(c);
            Commit commit = readCommit(c);
            queue.addAll(commit.parents());
        }

        queue.clear();
        queue.add(commitB);
        var visitedB = new java.util.HashSet<String>();

        while (!queue.isEmpty()) {
            String c = queue.poll();
            if (visitedB.contains(c)) continue;
            visitedB.add(c);

            if (ancestorsA.contains(c)) {
                return c; //lca
            }
            Commit commit = readCommit(c);
            queue.addAll(commit.parents());
        }
        return null;
    }

    /**
     * Returns a map of Commit Hash -> List of Ref Names (Branches, Tags).
     * Used for log decoration (e.g., "(HEAD -> master, tag: v1.0)").
     */
    public Map<String, List<String>> getRefsMap() throws IOException {
        Map<String, List<String>> refs = new HashMap<>();

        String headHash = resolveHead();
        String currentBranch = getCurrentBranch();
        if (headHash != null) {
            refs.computeIfAbsent(headHash, k -> new ArrayList<>()).add("HEAD -> " + currentBranch);
        }

        Path headsDir = resolveRefsDir().resolve("heads");
        if (Files.exists(headsDir)) {
            try (var stream = Files.list(headsDir)) {
                stream.forEach(path -> {
                    try {
                        String name = path.getFileName().toString();
                        if (!name.equals(currentBranch)) {
                            String hash = Files.readString(path).trim();
                            refs.computeIfAbsent(hash, k -> new ArrayList<>()).add("branch: " + name);
                        }
                    } catch (IOException e) { }
                });
            }
        }

        Path tagsDir = resolveRefsDir().resolve("tags");
        if (Files.exists(tagsDir)) {
            try (var stream = Files.list(tagsDir)) {
                stream.forEach(path -> {
                    try {
                        String name = path.getFileName().toString();
                        String hash = Files.readString(path).trim();
                        refs.computeIfAbsent(hash, k -> new ArrayList<>()).add("tag: " + name);
                    } catch (IOException e) { }
                });
            }
        }

        return refs;
    }

    /**
     * Performs a recursive 3-way merge of three trees.
     * Returns the hash of the resulting tree.
     */
    private String mergeTrees(String baseHash, String oursHash, String theirsHash) throws IOException, NoSuchAlgorithmException {
        if (oursHash.equals(theirsHash)) {
            return oursHash;
        }
        if (baseHash.equals(oursHash)) {
            return theirsHash;
        }
        if (baseHash.equals(theirsHash)) {
            return oursHash;
        }

        Tree base = readTree(baseHash);
        Tree ours = readTree(oursHash);
        Tree theirs = readTree(theirsHash);

        var allNames = new java.util.TreeSet<String>();
        base.entries().forEach(e -> allNames.add(e.name()));
        ours.entries().forEach(e -> allNames.add(e.name()));
        theirs.entries().forEach(e -> allNames.add(e.name()));

        var newEntries = new ArrayList<TreeEntry>();

        for (String name : allNames) {
            TreeEntry bEntry = findEntry(base, name);
            TreeEntry oEntry = findEntry(ours, name);
            TreeEntry tEntry = findEntry(theirs, name);

            String bHash = bEntry != null ? bEntry.hash() : null;
            String oHash = oEntry != null ? oEntry.hash() : null;
            String tHash = tEntry != null ? tEntry.hash() : null;

            TreeEntry finalEntry = null;

            if (Objects.equals(oHash, tHash)) {
                finalEntry = oEntry;
            } else if (Objects.equals(bHash, oHash)) {
                finalEntry = tEntry;
            } else if (Objects.equals(bHash, tHash)) {
                finalEntry = oEntry;
            } else {
                boolean oIsDir = oEntry != null && oEntry.mode().type() == main.domain.tree.TreeEntryModeType.DIRECTORY;
                boolean tIsDir = tEntry != null && tEntry.mode().type() == main.domain.tree.TreeEntryModeType.DIRECTORY;

                if (oIsDir && tIsDir) {
                    String mergedSubTree = mergeTrees(
                            bHash != null ? bHash : emptyTreeHash(),
                            oHash,
                            tHash
                    );
                    finalEntry = new TreeEntry(oEntry.mode(), name, mergedSubTree);
                } else {
                    throw new IllegalStateException("Merge Conflict in file: " + name);
                }
            }

            if (finalEntry != null) {
                newEntries.add(finalEntry);
            }
        }

        return writeObject(new Tree(newEntries));
    }

    private TreeEntry findEntry(Tree tree, String name) {
        return tree.entries().stream().filter(e -> e.name().equals(name)).findFirst().orElse(null);
    }

    private String emptyTreeHash() throws IOException, NoSuchAlgorithmException {
        return writeObject(new Tree(new ArrayList<>()));
    }

    /**
     * Computes the difference between two commits.
     * Uses recursive tree comparison.
     *
     * @param commitHashA The base commit hash.
     * @param commitHashB The target commit hash.
     * @return A list of changes (Added, Modified, Deleted).
     */
    public List<DiffEntry> diff(String commitHashA, String commitHashB) throws IOException {
        String treeHashA = readCommit(commitHashA).treeHash();
        String treeHashB = readCommit(commitHashB).treeHash();

        var diffs = new ArrayList<DiffEntry>();
        compareTrees(treeHashA, treeHashB, "", diffs);
        return diffs;
    }

    private void compareTrees(String treeA, String treeB, String pathPrefix, List<DiffEntry> diffs) throws IOException {
        if (Objects.equals(treeA, treeB)) {
            return;
        }

        var entriesA = getTreeEntriesMap(treeA);
        var entriesB = getTreeEntriesMap(treeB);

        var allKeys = new TreeSet<String>();
        allKeys.addAll(entriesA.keySet());
        allKeys.addAll(entriesB.keySet());

        for (String name : allKeys) {
            TreeEntry entryA = entriesA.get(name);
            TreeEntry entryB = entriesB.get(name);

            String fullPath = pathPrefix.isEmpty() ? name : pathPrefix + "/" + name;

            if (entryA == null) {
                // ADDED: oldHash=null, newHash=entryB.hash()
                if (isDir(entryB)) {
                    compareTrees(null, entryB.hash(), fullPath, diffs);
                } else {
                    diffs.add(new DiffEntry(ChangeType.ADDED, fullPath, null, entryB.hash()));
                }
            } else if (entryB == null) {
                if (isDir(entryA)) {
                    compareTrees(entryA.hash(), null, fullPath, diffs);
                } else {
                    diffs.add(new DiffEntry(ChangeType.DELETED, fullPath, entryA.hash(), null));
                }
            } else if (!entryA.hash().equals(entryB.hash())) {
                boolean isDirA = isDir(entryA);
                boolean isDirB = isDir(entryB);

                if (isDirA && isDirB) {
                    compareTrees(entryA.hash(), entryB.hash(), fullPath, diffs);
                } else if (!isDirA && !isDirB) {
                    diffs.add(new DiffEntry(ChangeType.MODIFIED, fullPath, entryA.hash(), entryB.hash()));
                } else {
                    diffs.add(new DiffEntry(ChangeType.DELETED, fullPath, entryA.hash(), null));
                    diffs.add(new DiffEntry(ChangeType.ADDED, fullPath, null, entryB.hash()));
                }
            }
        }
    }

    private Map<String, TreeEntry> getTreeEntriesMap(String treeHash) throws IOException {
        if (treeHash == null) {
            return Collections.emptyMap();
        }
        Tree tree = readTree(treeHash);
        var map = new HashMap<String, TreeEntry>();
        for (var entry : tree.entries()) {
            map.put(entry.name(), entry);
        }
        return map;
    }

    private boolean isDir(TreeEntry entry) {
        return entry.mode().type() == TreeEntryModeType.DIRECTORY;
    }

    public java.util.List<DiffEntry> status() throws IOException, NoSuchAlgorithmException {
        String headHash = resolveHead();

        java.util.Map<String, String> headFiles = new java.util.HashMap<>();
        if (headHash != null) {
            String treeHash = readCommit(headHash).treeHash();
            flattenTree(treeHash, "", headFiles);
        }

        var changes = new java.util.ArrayList<DiffEntry>();

        scanWorkspace(rootDirectory, "", headFiles, changes);

        for (String deletedPath : headFiles.keySet()) {
            changes.add(new DiffEntry(ChangeType.DELETED, deletedPath));
        }

        return changes;
    }

    /**
     * Recursively flattens a Tree object into a Map of "path/to/file" -> "blobHash".
     */
    private void flattenTree(String treeHash, String prefix, java.util.Map<String, String> map) throws IOException {
        Tree tree = readTree(treeHash);
        for (TreeEntry entry : tree.entries()) {
            String fullPath = prefix.isEmpty() ? entry.name() : prefix + "/" + entry.name();

            if (entry.mode().type() == main.domain.tree.TreeEntryModeType.DIRECTORY) {
                flattenTree(entry.hash(), fullPath, map);
            } else {
                map.put(fullPath, entry.hash());
            }
        }
    }

    /**
     * Scans the disk, computes hashes, and compares with HEAD.
     * Removes found files from the 'headFiles' map.
     */
    private void scanWorkspace(Path dir, String prefix, java.util.Map<String, String> headFiles, java.util.List<DiffEntry> changes) throws IOException, NoSuchAlgorithmException {
        try (var stream = Files.list(dir)) {
            var children = stream.filter(p -> !IGNORED_DIRS.contains(p.getFileName().toString())).toList();

            for (Path path : children) {
                String fileName = path.getFileName().toString();
                String relPath = prefix.isEmpty() ? fileName : prefix + "/" + fileName;

                if (Files.isDirectory(path)) {
                    scanWorkspace(path, relPath, headFiles, changes);
                } else {
                    String currentHash = computeFileHash(path);

                    if (headFiles.containsKey(relPath)) {
                        String oldHash = headFiles.get(relPath);
                        if (!oldHash.equals(currentHash)) {
                            changes.add(new DiffEntry(ChangeType.MODIFIED, relPath));
                        }
                        headFiles.remove(relPath);
                    } else {

                        changes.add(new DiffEntry(ChangeType.ADDED, relPath));
                    }
                }
            }
        }
    }

    /**
     * Computes the SHA-1 hash of a file on disk exactly as it would be stored (Blob),
     * but does NOT write it to the .git folder.
     */
    private String computeFileHash(Path file) throws IOException, NoSuchAlgorithmException {
        byte[] content = Files.readAllBytes(file);
        Blob blob = new Blob(content);

        var type = ObjectType.BLOB;
        var serializedContent = type.serializeContent(blob);

        var header = type.getName().getBytes();
        var length = String.valueOf(serializedContent.length).getBytes();

        var byteArrayOut = new ByteArrayOutputStream();
        byteArrayOut.write(header);
        byteArrayOut.write(' ');
        byteArrayOut.write(length);
        byteArrayOut.write(0);
        byteArrayOut.write(serializedContent);

        return computeHash(byteArrayOut.toByteArray());
    }

    private Path resolveDotGit() { return rootDirectory.resolve(DIR_GIT); }
    private Path resolveObjectsDir() { return resolveDotGit().resolve(DIR_OBJECTS); }
    private Path resolveRefsDir() { return resolveDotGit().resolve(DIR_REFS); }
    private Path resolveHeadFile() { return resolveDotGit().resolve(FILE_HEAD); }
    private Path resolveConfigFile() { return resolveDotGit().resolve(FILE_CONFIG); }

    private static void writeStringToFile(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private RawObject readRawObject(String hash) throws IOException {
        final var objectFile = getObjectPath(hash);

        try (var fileStream = new FileInputStream(objectFile.toFile());
             var inflaterStream = new InflaterInputStream(fileStream)) {

            final var typeStr = readHeaderToken(inflaterStream, ' ');
            final var type = ObjectType.byName(typeStr);

            final var lengthStr = readHeaderToken(inflaterStream, 0); // Null terminator
            final int length = Integer.parseInt(lengthStr);

            final var content = inflaterStream.readNBytes(length);
            return new RawObject(type, content);
        }
    }

    private String writeRawObject(RawObject raw) throws IOException, NoSuchAlgorithmException {
        final var header = raw.type.getName().getBytes();
        final var length = String.valueOf(raw.content.length).getBytes();

        // Prepare the uncompressed stream for hashing: "type length\0content"
        final var byteArrayOut = new ByteArrayOutputStream();
        byteArrayOut.write(header);
        byteArrayOut.write(' ');
        byteArrayOut.write(length);
        byteArrayOut.write(0);
        byteArrayOut.write(raw.content);

        final var fullData = byteArrayOut.toByteArray();
        final var hash = computeHash(fullData);

        storeCompressed(hash, fullData);
        return hash;
    }

    private void storeCompressed(String hash, byte[] data) throws IOException {
        final var objectPath = getObjectPath(hash);

        if (Files.exists(objectPath)) {
            return; // Idempotent write
        }

        Files.createDirectories(objectPath.getParent());

        try (var fileOut = Files.newOutputStream(objectPath);
             var deflaterOut = new DeflaterOutputStream(fileOut)) {
            deflaterOut.write(data);
        }
    }

    private Path getObjectPath(String hash) {
        return resolveObjectsDir()
                .resolve(hash.substring(0, 2))
                .resolve(hash.substring(2));
    }

    private String readHeaderToken(InputStream stream, int delimiter) throws IOException {
        final var sb = new StringBuilder();
        int b;
        while ((b = stream.read()) != -1 && b != delimiter) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    private String computeHash(byte[] data) throws NoSuchAlgorithmException {
        final var digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return HexFormat.of().formatHex(digest.digest(data));
    }

    private TreeEntry processTreeEntry(Path path) throws IOException, NoSuchAlgorithmException {
        String hash;
        TreeEntryMode mode;

        if (Files.isDirectory(path)) {
            hash = writeTree(path);
            mode = TreeEntryMode.directory();
        } else {
            hash = writeBlob(path);
            mode = determineFileMode(path);
        }
        return new TreeEntry(mode, path.getFileName().toString(), hash);
    }

    String writeBlob(Path path) throws IOException, NoSuchAlgorithmException {
        return writeObject(new Blob(Files.readAllBytes(path)));
    }

    private TreeEntryMode determineFileMode(Path path) throws IOException {
        if (Platform.isWindows()) {
            return TreeEntryMode.regularFile(0644);
        }
        return TreeEntryMode.regularFile(Files.readAttributes(path, PosixFileAttributes.class));
    }

    private void checkoutRecursive(Tree tree, Path currentDir) throws IOException {
        for (var entry : tree.entries()) {
            final var targetPath = currentDir.resolve(entry.name());

            switch (entry.mode().type()) {
                case REGULAR_FILE -> {
                    final var blob = readBlob(entry.hash());
                    Files.write(targetPath, blob.data(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                }
                case DIRECTORY -> {
                    Files.createDirectories(targetPath);
                    checkoutRecursive(readTree(entry.hash()), targetPath);
                }
                default -> throw new UnsupportedOperationException("Unsupported entry type: " + entry.mode().type());
            }
        }
    }

    /**
     * Returns the name of the current branch (e.g., "master") or "DETACHED HEAD".
     */
    public String getCurrentBranch() throws IOException {
        String headContent = Files.readString(resolveHeadFile()).trim();
        if (headContent.startsWith("ref: refs/heads/")) {
            return headContent.substring("ref: refs/heads/".length());
        }
        return "DETACHED HEAD (" + headContent + ")";
    }

    /**
     * Lists all local branches.
     */
    public List<String> listBranches() throws IOException {
        Path headsDir = resolveRefsDir().resolve("heads");
        if (!Files.exists(headsDir)) return List.of();

        try (var stream = Files.list(headsDir)) {
            return stream.map(p -> p.getFileName().toString()).toList();
        }
    }

    /**
     * Deletes a branch.
     * Throws error if trying to delete the currently checked out branch.
     */
    public void deleteBranch(String branchName) throws IOException {
        String current = getCurrentBranch();
        if (current.equals(branchName)) {
            throw new IllegalArgumentException("Cannot delete the branch you are currently on.");
        }

        Path branchFile = resolveRefsDir().resolve("heads").resolve(branchName);
        if (!Files.exists(branchFile)) {
            throw new NoSuchFileException("Branch not found: " + branchName);
        }
        Files.delete(branchFile);
    }

    public void updateRef(String refName, String newHash) throws IOException {
        // refName ex: "refs/heads/master"
        if (refName.startsWith("refs/")) {
            // Path relative to .git
            Path refFile = resolveDotGit().resolve(refName);
            Files.createDirectories(refFile.getParent());
            writeStringToFile(refFile, newHash);
        } else {
            // Assume branch name
            Path branchFile = resolveRefsDir().resolve("heads").resolve(refName);
            writeStringToFile(branchFile, newHash);
        }
    }

    public void unpackAndApply(byte[] packBytes) throws IOException, DataFormatException, NoSuchAlgorithmException {
        final var packParser = new PackParser(ByteBuffer.wrap(packBytes));
        final var packObjects = packParser.parse();

        for (var obj : packObjects) {
            if (obj instanceof PackObject.Undeltified(ObjectType type, byte[] content)) {
                writeRawObject(new RawObject(type, content));
            }
        }

        for (var obj : packObjects) {
            if (obj instanceof PackObject.Deltified d) {
                applyDelta(d);
            }
        }
    }

    private void applyDelta(PackObject.Deltified delta) throws IOException, NoSuchAlgorithmException {
        final var baseObject = readRawObject(delta.baseHash());
        final var buffer = ByteBuffer.allocate(delta.size());

        for (var inst : delta.instructions()) {
            if (inst instanceof DeltaInstruction.Copy(int offset, int size)) {
                buffer.put(baseObject.content, offset, size);
            } else if (inst instanceof DeltaInstruction.Insert(byte[] data)) {
                buffer.put(data);
            }
        }

        if (buffer.hasRemaining()) {
            throw new IllegalStateException("Delta application resulted in incomplete buffer.");
        }

        writeRawObject(new RawObject(baseObject.type, buffer.array()));
    }

    /**
     * Creates a lightweight tag pointing to the current HEAD commit.
     * Lightweight tags are simply references stored in .git/refs/tags/
     *
     * @param tagName The name of the tag (e.g., "v1.0").
     * @throws IOException If HEAD is invalid or tag already exists.
     */
    public void createTag(String tagName) throws IOException {
        String currentHash = resolveHead();
        if (currentHash == null) {
            throw new IOException("Cannot create tag: HEAD does not point to a valid commit (repo empty?)");
        }

        Path tagsDir = resolveRefsDir().resolve("tags");
        Files.createDirectories(tagsDir); // Asiguram ca folderul exista

        Path tagFile = tagsDir.resolve(tagName);
        if (Files.exists(tagFile)) {
            throw new FileAlreadyExistsException("Tag already exists: " + tagName);
        }

        writeStringToFile(tagFile, currentHash);
    }

    /**
     * Lists all tags in the repository.
     */
    public List<String> listTags() throws IOException {
        Path tagsDir = resolveRefsDir().resolve("tags");
        if (!Files.exists(tagsDir)) return List.of();

        try (var stream = Files.list(tagsDir)) {
            return stream.map(p -> p.getFileName().toString()).sorted().toList();
        }
    }

    /**
     * Archives the specific reference (branch/commit) into a ZIP stream.
     */
    public void archive(String ref, java.io.OutputStream out) throws IOException {
        String hash = resolveBranch(ref);
        if (hash == null) hash = resolveHead(); // Fallback la HEAD
        if (hash == null) throw new IOException("Reference not found: " + ref);

        Commit commit = readCommit(hash);
        Tree tree = readTree(commit.treeHash());

        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(out)) {
            archiveRecursive(tree, "", zos);
        }
    }

    private void archiveRecursive(Tree tree, String prefix, java.util.zip.ZipOutputStream zos) throws IOException {
        for (var entry : tree.entries()) {
            String path = prefix + entry.name();
            if (entry.mode().type() == main.domain.tree.TreeEntryModeType.DIRECTORY) {
                zos.putNextEntry(new java.util.zip.ZipEntry(path + "/"));
                zos.closeEntry();
                archiveRecursive(readTree(entry.hash()), path + "/", zos);
            } else {
                zos.putNextEntry(new java.util.zip.ZipEntry(path));
                Blob blob = readBlob(entry.hash());
                zos.write(blob.data());
                zos.closeEntry();
            }
        }
    }

    // Helper Records
    private record RawObject(ObjectType type, byte[] content) {}
    public record LogEntry(String hash, Commit commit) {}
    public record DiffEntry(ChangeType type, String path, String oldHash, String newHash) {
        public DiffEntry(ChangeType type, String path) {
            this(type, path, null, null);
        }
    }
}