package main;

import main.domain.AuthorSignature;
import main.domain.Commit;
import main.domain.ObjectType;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.InflaterInputStream;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, DataFormatException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        Path rootDir = Paths.get(".").toAbsolutePath().normalize();
        String command;
        String[] commandArgs;

        if (args[0].equals("-C")) {
            if (args.length < 3) {
                System.err.println("Error: -C flag requires a path and a command.");
                return;
            }
            rootDir = Paths.get(args[1]).toAbsolutePath().normalize();
            command = args[2];

            commandArgs = Arrays.copyOfRange(args, 3, args.length);
        } else {
            command = args[0];
            commandArgs = Arrays.copyOfRange(args, 1, args.length);
        }

        if (!Files.exists(rootDir) && !command.equals("init") && !command.equals("clone") && !command.equals("login") && !command.equals("logout") && !command.equals("whoami")) {
            System.err.println("Error: Directory does not exist: " + rootDir);
            return;
        }

        try {
            switch (command) {
                case "login" -> new AuthClient().login();
                case "logout" -> CredentialsManager.logout();
                case "whoami" -> {
                    String u = CredentialsManager.getUsername();
                    if (u != null) System.out.println("Logged in as: " + u);
                    else System.out.println("Not logged in.");
                }

                case "init" -> init(rootDir, commandArgs);
                case "clone" -> cloneRepo(rootDir, commandArgs);
                case "cat-file" -> catFile(rootDir, commandArgs);
                case "hash-object" -> hashObject(rootDir, commandArgs);
                case "ls-tree" -> lsTree(rootDir, commandArgs);
                case "write-tree" -> writeTree(rootDir);
                case "commit-tree" -> commitTree(rootDir, commandArgs);
                case "debug-object" -> debugObject(rootDir, commandArgs);
                case "branch" -> branch(rootDir, commandArgs);
                case "checkout" -> checkout(rootDir, commandArgs);
                case "push" -> push(rootDir, commandArgs);
                case "pull" -> pull(rootDir, commandArgs);
                case "log" -> log(rootDir, commandArgs);
                case "tag" -> tag(rootDir, commandArgs);
                case "merge" -> merge(rootDir, commandArgs);
                case "diff" -> diff(rootDir, commandArgs);
                case "status" -> status(rootDir);

                default -> {
                    System.err.println("Unknown command: " + command);
                    printUsage();
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
        }
    }

    private static void status(Path repoRoot) throws Exception {
        var git = Git.open(repoRoot);
        var changes = git.status();

        if (changes.isEmpty()) {
            System.out.println("Nothing to commit, working tree clean");
            return;
        }

        System.out.println("Changes:");
        for (var entry : changes) {
            String symbol = switch (entry.type()) {
                case ADDED -> "A";
                case MODIFIED -> "M";
                case DELETED -> "D";
            };
            System.out.println(symbol + "\t" + entry.path());
        }
    }

    private static void diff(Path repoRoot, String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: diff <commitA> <commitB>");
        }
        String hashA = args[0];
        String hashB = args[1];

        var git = Git.open(repoRoot);
        var changes = git.diff(hashA, hashB);

        for (var entry : changes) {
            String typeCode = switch (entry.type()) {
                case ADDED -> "A";
                case MODIFIED -> "M";
                case DELETED -> "D";
            };
            System.out.println(typeCode + "\t" + entry.path());
        }
    }

    private static void merge(Path repoRoot, String[] args) throws Exception {
        if (args.length < 1) throw new IllegalArgumentException("Usage: merge <branch>");
        String branch = args[0];

        var git = Git.open(repoRoot);
        String mergeCommit = git.merge(branch);
        System.out.println(mergeCommit);
    }

    private static void log(Path repoRoot, String[] args) throws IOException {
        var git = Git.open(repoRoot);
        var history = git.log();
        var refs = git.getRefsMap();

        if (history.isEmpty()) {
            System.out.println("No commits yet.");
            return;
        }

        for (var entry : history) {
            Commit c = entry.commit();
            String hash = entry.hash();

            String decoration = "";
            if (refs.containsKey(hash)) {
                decoration = " (" + String.join(", ", refs.get(hash)) + ")";
            }

            System.out.println("commit " + hash + decoration);

            if (c.parents().size() > 1) {
                System.out.print("Merge:");
                for (String p : c.parents()) {
                    System.out.print(" " + p.substring(0, 7));
                }
                System.out.println();
            }

            System.out.println("Author: " + c.author().name() + " <" + c.author().email() + ">");
            System.out.println("Date:   " + c.author().timestamp());
            System.out.println();
            System.out.println("    " + c.message());
            System.out.println();
            System.out.println("---");
        }
    }

    private static void tag(Path repoRoot, String[] args) throws IOException {
        var git = Git.open(repoRoot);

        if (args.length == 0) {
            List<String> tags = git.listTags();
            if (tags.isEmpty()) {
                System.out.println("No tags found.");
            } else {
                tags.forEach(System.out::println);
            }
            return;
        }

        String tagName = args[0];
        try {
            git.createTag(tagName);
            System.out.println("Created tag '" + tagName + "'");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void push(Path repoRoot, String[] args) throws Exception {
        if (args.length < 2) throw new IllegalArgumentException("Usage: push <remote_url> <branch>");

        String token = CredentialsManager.getToken();
        if (token == null) {
            System.err.println("Error: You must be logged in to push. Run 'git-cli login' first.");
            return;
        }

        String remoteUrl = args[0];
        String branch = args[1];

        var git = Git.open(repoRoot);
        git.push(remoteUrl, branch, token);
        System.out.println("Push completed to " + remoteUrl);
    }

    private static void pull(Path repoRoot, String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: pull <remote_url> <branch>");
        }

        String token = CredentialsManager.getToken();
        if (token == null) {
            System.err.println("Error: You must be logged in to pull. Run 'git-cli login' first.");
            return;
        }

        String remoteUrl = args[0];
        String branch = args[1];

        var git = Git.open(repoRoot);
        System.out.println("Pulling from " + remoteUrl + " (" + branch + ")...");

        git.pull(remoteUrl, branch, token);

        System.out.println("Pull completed successfully.");
    }

    private static void checkout(Path repoRoot, String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: checkout <name>");
        }
        var git = Git.open(repoRoot);
        git.checkout(args[0]);
        System.out.println("Switched to branch '" + args[0] + "'");
    }

    private static void branch(Path repoRoot, String[] args) throws IOException {
        var git = Git.open(repoRoot);

        if (args.length == 0) {
            String current = git.getCurrentBranch();
            List<String> branches = git.listBranches();

            for (String b : branches) {
                if (b.equals(current)) {
                    System.out.println("* " + b);
                } else {
                    System.out.println("  " + b);
                }
            }
            return;
        }

        if (args[0].equals("-d")) {
            if (args.length < 2) {
                System.err.println("Usage: branch -d <name>");
                return;
            }
            try {
                git.deleteBranch(args[1]);
                System.out.println("Deleted branch " + args[1]);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            return;
        }

        git.createBranch(args[0]);
        System.out.println("Created branch '" + args[0] + "'");
    }

    private static void debugObject(Path repoRoot, String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: debug-object <hash>");
            return;
        }
        String hash = args[0];

        Path objPath = repoRoot.resolve(".git").resolve("objects")
                .resolve(hash.substring(0, 2)).resolve(hash.substring(2));

        if (!Files.exists(objPath)) {
            System.err.println("File not found: " + objPath);
            return;
        }

        System.out.println("Decoding object: " + hash + "\n\n");
        try (InputStream fis = new FileInputStream(objPath.toFile());
             InflaterInputStream iis = new InflaterInputStream(fis);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            int b;
            while ((b = iis.read()) != -1) {
                buffer.write(b);
            }

            String content = buffer.toString(StandardCharsets.UTF_8);
            System.out.println(content);

            System.out.println("\nHex dump: first 50 bytes");
            byte[] bytes = buffer.toByteArray();
            for(int i=0; i<Math.min(bytes.length, 50); i++) {
                System.out.printf("%02X ", bytes[i]);
            }
            System.out.println();

        } catch (Exception e) {
            System.err.println("ERROR: Could not inflate object. Is it corrupted? " + e.getMessage());
        }
    }

    private static void init(Path currentDir, String[] args) throws IOException {
        Path target = currentDir;
        if (args.length > 0) {
            target = currentDir.resolve(args[0]);
        }

        Git.init(target);
        System.out.println("Initialized empty Git repository in " + target);
    }

    private static void cloneRepo(Path currentDir, String[] args) throws IOException, NoSuchAlgorithmException, DataFormatException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: clone <url> <directory>");
        }
        URI uri = URI.create(args[0]);
        Path targetDir = currentDir.resolve(args[1]);
        String token = CredentialsManager.getToken();

        System.out.println("Cloning into '" + targetDir + "'...");
        Git.clone(uri, targetDir, token);
        System.out.println("Clone completed.");
    }

    private static void catFile(Path repoRoot, String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: cat-file -p <hash | HEAD | branch>");
        }
        String inputRef = args[args.length - 1];
        final var git = Git.open(repoRoot);

        String hash = inputRef;

        if (inputRef.equals("HEAD")) {
            hash = git.resolveHead();
            if (hash == null) {
                System.err.println("Fatal: HEAD does not point to a valid commit (empty repo?)");
                return;
            }
        }
        else {
            String branchHash = git.resolveBranch(inputRef);
            if (branchHash != null) {
                hash = branchHash;
            }
        }

        try {
            var blob = git.readBlob(hash);
            System.out.write(blob.data());
            System.out.flush();
            return;
        } catch (Exception ignored) {}

        try {
            var commit = git.readObject(ObjectType.COMMIT, hash);
            System.out.println("tree " + commit.treeHash());
            for (String p : commit.parents()) {
                System.out.println("parent " + p);
            }
            System.out.println("author " + commit.author().name() + " <" + commit.author().email() + ">");
            System.out.println("committer " + commit.committer().name() + " <" + commit.committer().email() + ">");
            System.out.println();
            System.out.println(commit.message());
            return;
        } catch (Exception ignored) {}

        try {
            var tree = git.readTree(hash);
            for (var entry : tree.entries()) {
                System.out.printf("%s %s %s\t%s%n", entry.mode().format(), "blob", entry.hash(), entry.name());
            }
            return;
        } catch (Exception ignored) {}

        System.err.println("Fatal: Not a valid object name " + inputRef);
    }

    private static void hashObject(Path repoRoot, String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: hash-object [-w] <file>");
        }

        boolean write = false;
        String filePathString = args[args.length - 1];

        for (String arg : args) {
            if (arg.equals("-w")) {
                write = true;
                break;
            }
        }

        final var git = Git.open(repoRoot);
        Path file = repoRoot.resolve(filePathString);

        if (!Files.exists(file)) {
            throw new IllegalArgumentException("File not found: " + file);
        }

        if (write) {
            System.out.println(git.writeBlob(file));
        } else {
            System.out.println(git.writeBlob(file));
        }
    }

    private static void lsTree(Path repoRoot, String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: ls-tree <tree-ish>");
        }
        String hash = args[args.length - 1];

        final var git = Git.open(repoRoot);
        final var tree = git.readTree(hash);

        for (final var entry : tree.entries()) {
            System.out.printf("%s %s %s\t%s%n", entry.mode().format(), "blob", entry.hash(), entry.name());
        }
    }

    private static void writeTree(Path repoRoot) throws IOException, NoSuchAlgorithmException {
        final var git = Git.open(repoRoot);
        System.out.println(git.writeTree(repoRoot));
    }

    private static void commitTree(Path repoRoot, String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: commit-tree <tree_hash> [-p parent] [-m message]");
        }

        String treeHash = args[0];
        List<String> parents = new ArrayList<>();
        String message = "no message";

        for (int i = 1; i < args.length; i++) {
            if ("-p".equals(args[i]) && i + 1 < args.length) {
                parents.add(args[++i]);
            } else if ("-m".equals(args[i]) && i + 1 < args.length) {
                message = args[++i];
            }
        }

        final var git = Git.open(repoRoot);
        final var author = AuthorSignature.now("User", "user@example.com");

        final var commit = new Commit(treeHash, parents, author, author, message);
        String commitHash = git.writeObject(commit);

        System.out.println(commitHash);

        Path headFile = repoRoot.resolve(".git/HEAD");
        if (Files.exists(headFile)) {
            String headContent = Files.readString(headFile).trim();
            if (headContent.startsWith("ref: ")) {
                String refPath = headContent.substring(5);
                Path refFile = repoRoot.resolve(".git").resolve(refPath);
                Files.createDirectories(refFile.getParent());
                Files.writeString(refFile, commitHash);
            }
        }
    }

    private static void printUsage() {
        System.out.println("""
            Usage: git [-C <path>] <command> [<args>]
            
            Core Commands:
              login                         Authenticate with the server
              logout                        Clear stored credentials
              whoami                        Display current user
              init [dir]                    Initialize a repository
              clone <url> <dir>             Clone a repository
            
            History & State:
              log                           Show commit logs
              status                        Show the working tree status
              diff                          Show changes between commits/tree
            
            Branching & Merging:
              branch                        Lists all branches (* marks the current one)
              branch <name>                 Create a new branch
              branch -d <name>              Deletes a branch
              checkout <name>               Switch to a branch
              merge <branch>                Join two or more development histories
            
            Sharing:
              push <remote> <branch>        Update remote refs
              pull <remote> <branch>        Fetch and merge remote changes
            
            Low-level (Plumbing):
              cat-file -p <hash>            Display object content
              hash-object [-w] <file>       Compute object ID
              ls-tree <tree-ish>            List tree content
              write-tree                    Write tree from current dir
              commit-tree <tree> ...        Create a commit
              
            Extras:
              tag                           Lists all tags  
              tag <name>                    Create a tag on current HEAD
            """);
    }
}

/*
teste windows manuale:
Mai intai se ruleaza:
mvn clean
mvn compile

Init:
mvn --% exec:java -Dexec.mainClass="main.Main" -Dexec.args="init test-project"

Apoi hash object:

Set-Content -Path "test-project\test.txt" -Value "Salut"
mvn --% exec:java -Dexec.mainClass="main.Main" -Dexec.args="-C test-project hash-object -w test.txt"
SE COPIAZA BLOB_HASH
67159ae4cfb9291705f06ed368fb9ce52da3fa49

Apoi Write tree:
mvn --% exec:java -Dexec.mainClass="main.Main" -Dexec.args="-C test-project write-tree"
SE COPIAZA TREE_HASH
1bbd2eaa9332317e81536c3b77670a786723a477

Apoi Commit tree:
mvn --% exec:java -Dexec.mainClass="main.Main" -Dexec.args="-C test-project commit-tree <TREE_HASH> -m 'InitialCommit1'"
SE COPIAZA COMMIT_HASH
2ede84b050db84775041934f62a6bf3d46d89263

Si la final sa vedem ce s-a facut:
mvn --% exec:java -Dexec.mainClass="main.Main" -Dexec.args="-C test-project cat-file -p <COMMIT_HASH | BLOB_HASH | TREE_HASH | etc.>"

Apoi test clonare:
mvn --% exec:java -Dexec.mainClass="main.Main" -Dexec.args="clone https://github.com/octocat/Hello-World.git demo-clone"

 */