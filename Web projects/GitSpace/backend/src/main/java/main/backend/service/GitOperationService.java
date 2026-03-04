package main.backend.service;

import main.Git;
import main.backend.dto.pr.PullRequestFileDto;
import main.backend.dto.repo.FileOperationRequest;
import main.backend.dto.repo.GitCommitDto;
import main.backend.dto.repo.GitTreeEntryDto;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.User;
import main.backend.repository.RepositoryRepository;
import main.domain.AuthorSignature;
import main.domain.Blob;
import main.domain.Commit;
import main.domain.Tree;
import main.domain.tree.TreeEntry;
import main.domain.tree.TreeEntryModeType;
import main.util.ChangeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// import com.gitclone.core.GitTreeEntry;
// import com.gitclone.core.AuthorSignature;

/**
 * This is the main bridge.
 * Corresponds to Issues 9, 29, 30.
 */
@Service
public class GitOperationService {

    // TODO: Inject RepoMetadataService (to find disk paths)

    private final RepositoryRepository repositoryRepository;
    private final Path gitRootPath;

    @Autowired
    public GitOperationService(RepositoryRepository repositoryRepository,
                               @Value("${git.storage.path}") String gitRootPathStr) {
        this.repositoryRepository = repositoryRepository;
        this.gitRootPath = Path.of(gitRootPathStr);
    }

    /**
     * Helper to get a ready-to-use repo object from your core lib.
     */
//    private GitRepository getCoreRepo(String username, String repoName) {
//        // TODO:
//        // 1. Repository metadata = repoMetadataService.getRepo(username, repoName)
//        // 2. Path diskPath = Paths.get(metadata.getDiskPath())
//        // 3. return GitRepository.open(diskPath);
//        throw new UnsupportedOperationException("TODO: Implement getCoreRepo helper");
//    }

    public List<PullRequestFileDto> getPullRequestDiff(String username, String repoName, String targetBranch, String sourceBranch) throws IOException {
        main.Git git = openLibRepo(username, repoName);

        String targetHash = git.resolveBranch(targetBranch);
        String sourceHash = git.resolveBranch(sourceBranch);

        if (targetHash == null || sourceHash == null) {
            return List.of();
        }

        String baseHash = git.findMergeBase(targetHash, sourceHash);
        if (baseHash == null) baseHash = targetHash; // Fallback

        List<main.Git.DiffEntry> diffEntries = git.diff(baseHash, sourceHash);

        List<PullRequestFileDto> result = new ArrayList<>();

        for (var entry : diffEntries) {
            String diffText = "";

            try {
                String oldContent = (entry.oldHash() != null) ? new String(git.readBlob(entry.oldHash()).data()) : "";
                String newContent = (entry.newHash() != null) ? new String(git.readBlob(entry.newHash()).data()) : "";

                diffText = generateSimpleDiff(oldContent, newContent);
            } catch (Exception e) {
                diffText = "Binary file or too large to display.";
            }

            result.add(new PullRequestFileDto(
                    entry.type().name(), // "ADDED", "MODIFIED", etc.
                    entry.path(),
                    diffText
            ));
        }

        return result;
    }

    private String generateSimpleDiff(String oldText, String newText) {
        String[] oldLines = oldText.isEmpty() ? new String[0] : oldText.split("\\r?\\n");
        String[] newLines = newText.isEmpty() ? new String[0] : newText.split("\\r?\\n");

        StringBuilder sb = new StringBuilder();

        // ADDED -> totul e cu +
        // DELETED -> totul e cu -
        // MODIFIED -> linie cu linie

        if (oldLines.length == 0) {
            for (String line : newLines) sb.append("+").append(line).append("\n");
        } else if (newLines.length == 0) {
            for (String line : oldLines) sb.append("-").append(line).append("\n");
        } else {
            int max = Math.max(oldLines.length, newLines.length);
            for (int i = 0; i < max; i++) {
                String o = (i < oldLines.length) ? oldLines[i] : null;
                String n = (i < newLines.length) ? newLines[i] : null;

                if (o != null && n != null && o.equals(n)) {
                    sb.append(" ").append(o).append("\n"); // Neschimbat
                } else {
                    if (o != null) sb.append("-").append(o).append("\n");
                    if (n != null) sb.append("+").append(n).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public void mergePullRequest(String username, String repoName, String targetBranch, String sourceBranch) throws Exception {
        Git git = openLibRepo(username, repoName);

        git.checkout(targetBranch);
        git.merge(sourceBranch);
    }

    private String mapChangeType(ChangeType type) {
        return switch (type) {
            case ADDED -> "added";
            case MODIFIED -> "modified";
            case DELETED -> "deleted";
        };
    }

    public void createNewRepository(Path diskPath) throws IOException {
        Git.init(diskPath);
    }

    public void deleteRepository(Path diskPath) throws IOException {
        if (Files.exists(diskPath)) {
            // folder .git
            FileSystemUtils.deleteRecursively(diskPath);
        }
    }

    public void renameRepository(Path oldPath, Path newPath) throws IOException {
        if (Files.exists(oldPath)) {
            Files.move(oldPath, newPath, StandardCopyOption.ATOMIC_MOVE);
        }
    }

    public List<?> listTree(String username, String repoName, String branchName, String subPath) throws IOException {
        Git git = openLibRepo(username, repoName);

        // commit care pointeaza catre branch
        String commitHash = resolveBranchHash(git, branchName);
        Commit commit = git.readCommit(commitHash);

        // root tree
        Tree tree = git.readTree(commit.treeHash());

        // folder cautat
        if (subPath != null && !subPath.isBlank()) {
            tree = navigateToSubTree(git, tree, subPath);
        }

        // tree -> dto
        return tree.entries().stream()
                .map(entry -> new GitTreeEntryDto(
                        entry.name(),
                        entry.mode().type() == TreeEntryModeType.DIRECTORY ? "DIRECTORY" : "FILE",
                        entry.hash(),
                        entry.mode().toString()
                ))
                .sorted(Comparator.comparing(GitTreeEntryDto::type)) // Folders first
                .collect(Collectors.toList());
    }

    public byte[] getFileContent(String username, String repoName, String branchName, String filePath) throws IOException {
        Git git = openLibRepo(username, repoName);

        String commitHash = resolveBranchHash(git, branchName);
        Commit commit = git.readCommit(commitHash);
        Tree rootTree = git.readTree(commit.treeHash());

        String blobHash = findBlobHash(git, rootTree, filePath);

        Blob blob = git.readBlob(blobHash);
        return blob.data();
    }

    public byte[] getReadmeContent(String username, String repoName) throws IOException {
        try {
            // Incercam sa citim README.md de pe master/main
            // TODO: Detect default branch dynamically
            return getFileContent(username, repoName, "master", "README.md");
        } catch (ResourceNotFoundException e) {
            return null; // No readme found
        }
    }

    public List<GitCommitDto> getCommitHistory(String username, String repoName, String branchName) throws IOException {
        Git git = openLibRepo(username, repoName);

        String startHash = resolveBranchHash(git, branchName);
        return traverseLog(git, startHash);
    }

    public List<String> getBranches(String username, String repoName) throws IOException {
        Git git = openLibRepo(username, repoName);
        return git.listBranches();
    }

    // TODO: Add methods for getTags

    // Helpers
    private Git openLibRepo(String username, String repoName) throws IOException {
        // Validam in DB mai intai ca sa evitam path traversal
        // Scoatem .git daca e in nume, dar fisierul pe disc are .git
        String cleanName = repoName.endsWith(".git") ? repoName.substring(0, repoName.length() - 4) : repoName;

        repositoryRepository.findByOwnerUsernameAndName(username, cleanName)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found in DB"));

        Path repoDiskPath = gitRootPath.resolve(username).resolve(cleanName + ".git");
        return Git.open(repoDiskPath);
    }

    private String resolveBranchHash(Git git, String branchName) throws IOException {
        // Citim manual refs/heads/<branchName>
        String hash = git.resolveBranch(branchName);

        if (hash == null) {
            // Poate e detached head sau default branch e altul
            throw new ResourceNotFoundException("Branch not found: " + branchName);
        }

        return hash;
    }

    private Tree navigateToSubTree(Git git, Tree currentTree, String path) throws IOException {
        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            TreeEntry entry = currentTree.entries().stream()
                    .filter(e -> e.name().equals(part))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Path not found: " + path));

            if (entry.mode().type() != TreeEntryModeType.DIRECTORY) {
                throw new ResourceNotFoundException("Path is not a directory: " + path);
            }

            currentTree = git.readTree(entry.hash());
        }
        return currentTree;
    }

    private String findBlobHash(Git git, Tree currentTree, String path) throws IOException {
        String[] parts = path.split("/");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;

            boolean isLast = (i == parts.length - 1);

            TreeEntry entry = currentTree.entries().stream()
                    .filter(e -> e.name().equals(part))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("File not found: " + path));

            if (isLast) {
                if (entry.mode().type() == TreeEntryModeType.DIRECTORY) {
                    throw new ResourceNotFoundException("Expected file but found directory: " + path);
                }
                return entry.hash();
            } else {
                if (entry.mode().type() != TreeEntryModeType.DIRECTORY) {
                    throw new ResourceNotFoundException("Path segment is not a directory: " + part);
                }
                currentTree = git.readTree(entry.hash());
            }
        }
        throw new ResourceNotFoundException("File not found: " + path);
    }

    // BFS pornind de la un Hash specific
    private List<GitCommitDto> traverseLog(Git git, String startHash) throws IOException {
        var history = new java.util.ArrayList<GitCommitDto>();
        var visited = new java.util.HashSet<String>();
        var queue = new java.util.ArrayDeque<String>();

        queue.add(startHash);

        while (!queue.isEmpty()) {
            String hash = queue.poll();
            if (visited.contains(hash)) continue;
            visited.add(hash);

            try {
                Commit commit = git.readCommit(hash);

                // Convertim la DTO
                history.add(new GitCommitDto(
                        hash,
                        commit.message().split("\n")[0], // Short msg
                        commit.message(),
                        commit.author().name(),
                        commit.author().email(),
                        commit.author().timestamp().toInstant(),
                        commit.parents()
                ));

                queue.addAll(commit.parents());
            } catch (Exception e) {
                System.err.println("Warning: Could not read commit " + hash);
            }
        }
        return history;
    }

    /**
     * Handles Create or Update file via Web UI.
     */
    public void commitFile(String username, String repoName, String filePath, FileOperationRequest request, User user) throws Exception {
        Git git = openLibRepo(username, repoName);

        Path repoPath = gitRootPath.resolve(username).resolve(repoName.endsWith(".git") ? repoName : repoName + ".git");

        String parentHash = git.resolveBranch(request.branch());
        boolean isNewBranch = (parentHash == null);

        if (!isNewBranch) {
            git.checkout(request.branch());
        }

        Path targetFile = repoPath.resolve(filePath);

        if (targetFile.getParent() != null) {
            Files.createDirectories(targetFile.getParent());
        }

        Files.writeString(targetFile, request.content());

        String newTreeHash = git.writeTree(repoPath);

        List<String> parents = parentHash != null ? List.of(parentHash) : List.of();

        String authorName = (user.getFullName() != null && !user.getFullName().isBlank())
                ? user.getFullName()
                : user.getUsername();
        String authorEmail = (user.getEmail() != null && !user.getEmail().isBlank())
                ? user.getEmail()
                : user.getUsername() + "@localhost";
        AuthorSignature author = new AuthorSignature(authorName, authorEmail, ZonedDateTime.now());

        Commit commit = new Commit(
                newTreeHash,
                parents,
                author, // Author
                author, // Committer
                request.message()
        );

        String newCommitHash = git.writeObject(commit);

        git.updateRef("refs/heads/" + request.branch(), newCommitHash);

    }

    /**
     * Handles Delete file via Web UI.
     */
    public void deleteFile(String username, String repoName, String filePath, FileOperationRequest request, User user) throws Exception {
        Git git = openLibRepo(username, repoName);
        git.checkout(request.branch());

        Path repoPath = gitRootPath.resolve(username).resolve(repoName.endsWith(".git") ? repoName : repoName + ".git");
        Path targetFile = repoPath.resolve(filePath);

        if (Files.exists(targetFile)) {
            Files.delete(targetFile);
        } else {
            throw new ResourceNotFoundException("File not found: " + filePath);
        }

        String newTreeHash = git.writeTree(repoPath);
        String parentHash = git.resolveBranch(request.branch());
        List<String> parents = parentHash != null ? List.of(parentHash) : List.of();

        String authorName = (user.getFullName() != null && !user.getFullName().isBlank())
                ? user.getFullName()
                : user.getUsername();
        String authorEmail = (user.getEmail() != null && !user.getEmail().isBlank())
                ? user.getEmail()
                : user.getUsername() + "@localhost";
        AuthorSignature author = new AuthorSignature(authorName, authorEmail, ZonedDateTime.now());
        Commit commit = new Commit(newTreeHash, parents, author, author, request.message());
        String newCommitHash = git.writeObject(commit);

        git.updateRef("refs/heads/" + request.branch(), newCommitHash);
    }


    public Map<String, Object> getRepoStatistics(String username, String repoName, String branch) throws IOException {
        Git git = openLibRepo(username, repoName);
        String startHash = resolveBranchHash(git, branch);

        var history = traverseLog(git, startHash);

        int totalCommits = history.size();

        long uniqueContributors = history.stream()
                .map(GitCommitDto::authorEmail)
                .distinct()
                .count();

        long codebaseSize = 0;
        try {
            main.domain.Commit latest = git.readCommit(startHash);
            codebaseSize = calculateTreeSize(git, latest.treeHash());
        } catch (Exception ignored) {
        }

        return Map.of(
                "totalCommits", totalCommits,
                "contributors", uniqueContributors,
                "size", codebaseSize,
                "activity", history
        );
    }

    private long calculateTreeSize(Git git, String treeHash) throws IOException {
        long size = 0;
        main.domain.Tree tree = git.readTree(treeHash);
        for (var entry : tree.entries()) {
            if (entry.mode().type() == main.domain.tree.TreeEntryModeType.DIRECTORY) {
                size += calculateTreeSize(git, entry.hash());
            } else {
                size += git.readBlob(entry.hash()).data().length;
            }
        }
        return size;
    }

    public boolean checkMergeability(String username, String repoName, String targetBranch, String sourceBranch) throws IOException {
        main.Git git = openLibRepo(username, repoName);

        return git.canMerge(targetBranch, sourceBranch);
    }

    public void downloadArchive(String username, String repoName, String branch, java.io.OutputStream out) throws IOException {
        main.Git git = openLibRepo(username, repoName);
        git.archive(branch, out);
    }
}
