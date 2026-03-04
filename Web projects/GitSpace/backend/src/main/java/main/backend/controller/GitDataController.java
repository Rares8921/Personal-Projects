package main.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import main.backend.dto.repo.GitCommitDto;
import main.backend.dto.repo.GitTreeEntryDto;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.Repository;
import main.backend.model.User;
import main.backend.repository.RepositoryRepository;
import main.backend.service.AccessControlService;
import main.backend.service.GitOperationService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.List;

/**
 *  * Handles reading Git data *from* a repository.
 * Used by the Frontend to display the code browser.
 */
@RestController
@RequestMapping("/api/repos/{username}/{repoName}/git")
public class GitDataController {


    // TODO: Inject MarkdownService (for Issue 9)
    private final GitOperationService gitOperationService;
    private final RepositoryRepository repositoryRepository;
    private final AccessControlService accessControlService;
    private final UserService userService;

    @Autowired
    public GitDataController(GitOperationService gitOperationService,
                            RepositoryRepository repositoryRepository,
                            AccessControlService accessControlService,
                            UserService userService) {
        this.gitOperationService = gitOperationService;
        this.repositoryRepository = repositoryRepository;
        this.accessControlService = accessControlService;
        this.userService = userService;
    }

    private void checkRepositoryAccess(String username, String repoName, UserDetails userDetails) {
        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        User currentUser = null;
        if (userDetails != null) {
            currentUser = userService.findByUsername(userDetails.getUsername());
        }

        if (!accessControlService.canViewRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found");
        }
    }

    /**
     * Get Tree (File List) for a branch and sub-path.
     * Example: GET /api/repos/user/repo/git/tree/master/src/main
     */
    @GetMapping("/tree/{branch}/**")
    public ResponseEntity<List<GitTreeEntryDto>> getTree(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable String branch,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) throws IOException {
        checkRepositoryAccess(username, repoName, userDetails);
        String fullPath = extractPathFromPattern(request);
        return ResponseEntity.ok((List<GitTreeEntryDto>) gitOperationService.listTree(username, repoName, branch, fullPath));
    }

    /**
     * Get Root Tree (no sub-path).
     */
    @GetMapping("/tree/{branch}")
    public ResponseEntity<List<GitTreeEntryDto>> getRootTree(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable String branch,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        checkRepositoryAccess(username, repoName, userDetails);
        return ResponseEntity.ok((List<GitTreeEntryDto>) gitOperationService.listTree(username, repoName, branch, null));
    }

    /**
     * Get File Content (Blob).
     */
    @GetMapping("/blob/{branch}/**")
    public ResponseEntity<byte[]> getBlob(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable String branch,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) throws IOException {
        checkRepositoryAccess(username, repoName, userDetails);
        String fullPath = extractPathFromPattern(request);
        byte[] content = gitOperationService.getFileContent(username, repoName, branch, fullPath);

        // Detectare simplista MIME type sau text/plain
        MediaType mediaType = MediaType.TEXT_PLAIN;
        // Daca vrei download, poti pune APPLICATION_OCTET_STREAM

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(content);
    }

    /**
     * Get Commit History.
     */
    @GetMapping("/commits/{branch}")
    public ResponseEntity<List<GitCommitDto>> getCommits(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable String branch,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        checkRepositoryAccess(username, repoName, userDetails);
        return ResponseEntity.ok(gitOperationService.getCommitHistory(username, repoName, branch));
    }

    /**
     * Get Branches for a repository.
     */
    @GetMapping("/branches")
    public ResponseEntity<List<String>> getBranches(
            @PathVariable String username,
            @PathVariable String repoName,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        checkRepositoryAccess(username, repoName, userDetails);
        return ResponseEntity.ok(gitOperationService.getBranches(username, repoName));
    }

    /**
     * Extract the wildcard path (everything after /tree/branch/...)
     */
    private String extractPathFromPattern(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        // Spring AntPathMatcher logic extraction
        org.springframework.util.AntPathMatcher apm = new org.springframework.util.AntPathMatcher();
        return apm.extractPathWithinPattern(bestMatchPattern, path);
    }
}