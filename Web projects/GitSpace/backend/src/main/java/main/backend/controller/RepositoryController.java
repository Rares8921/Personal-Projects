package main.backend.controller;

import jakarta.validation.Valid;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.dto.repo.RepositoryDto;
import main.backend.dto.repo.UpdateRepoRequest;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.Repository;
import main.backend.model.User;
import main.backend.repository.RepositoryRepository;
import main.backend.service.AccessControlService;
import main.backend.service.GitOperationService;
import main.backend.service.RepoMetadataService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles repository metadata (creating, deleting, listing).
 */
@RestController
@RequestMapping("/api/repos")
public class RepositoryController {

    private final RepoMetadataService repoService;
    private final UserService userService;
    private final RepositoryRepository repositoryRepository;
    private final AccessControlService accessControlService;
    private final GitOperationService gitOperationService;

    @Autowired
    public RepositoryController(RepoMetadataService repoService,
                                UserService userService,
                                RepositoryRepository repositoryRepository,
                                AccessControlService accessControlService,
                                GitOperationService gitOperationService) {
        this.repoService = repoService;
        this.userService = userService;
        this.repositoryRepository = repositoryRepository;
        this.accessControlService = accessControlService;
        this.gitOperationService = gitOperationService;
    }

    @PostMapping
    public ResponseEntity<RepositoryDto> createRepository(
            @Valid @RequestBody CreateRepoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        RepositoryDto createdRepo = repoService.createRepo(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRepo);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<RepositoryDto>> getUserRepositories(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails // Cine face cererea
    ) {
        User currentUser = null;
        if (userDetails != null) {
            currentUser = userService.findByUsername(userDetails.getUsername());
        }

        return ResponseEntity.ok(repoService.getReposByOwner(username, currentUser));
    }

    /**
     * Get Single Repository.
     */
    @GetMapping("/{username}/{repoName}")
    public ResponseEntity<RepositoryDto> getRepository(
            @PathVariable String username,
            @PathVariable String repoName,
            @AuthenticationPrincipal UserDetails userDetails // Null daca userul nu e logat
    ) {

        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        User currentUser = null;
        if (userDetails != null) {
            currentUser = userService.findByUsername(userDetails.getUsername());
        }

        if (!accessControlService.canViewRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This repository does not exist.");
        }

        return ResponseEntity.ok(repoService.getRepo(username, repoName));
    }

    /**
     * Get Public Repositories (Paginated).
     * Usage: GET /api/repos/explore?page=0&size=20
     */
    @GetMapping("/explore")
    public ResponseEntity<Page<RepositoryDto>> getExploreRepositories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (size > 100) size = 100;

        return ResponseEntity.ok(repoService.getTopRepositories(page, size));
    }

    /**
     * Update Repository.
     */
    @PutMapping("/{username}/{repoName}")
    public ResponseEntity<RepositoryDto> updateRepository(
            @PathVariable String username,
            @PathVariable String repoName,
            @Valid @RequestBody UpdateRepoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUsername(userDetails.getUsername());
        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        if (!accessControlService.canWriteRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this repository.");
        }

        return ResponseEntity.ok(repoService.updateRepo(username, repoName, request));
    }

    /**
     * Delete Repository.
     */
    @DeleteMapping("/{username}/{repoName}")
    public ResponseEntity<Void> deleteRepository(
            @PathVariable String username,
            @PathVariable String repoName,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUsername(userDetails.getUsername());
        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));

        if (!accessControlService.canWriteRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this repository.");
        }

        repoService.deleteRepo(username, repoName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/{repoName}/stats")
    public ResponseEntity<?> getStatistics(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "master") String branch,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));

        User currentUser = userService.findByUsername(userDetails.getUsername());

        if (!accessControlService.isOwner(currentUser, repo) && !accessControlService.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to statistics");
        }

        Map<String, Object> stats = gitOperationService.getRepoStatistics(username, repoName, branch);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{username}/{repoName}/branches")
    public ResponseEntity<List<String>> listBranches(
            @PathVariable String username,
            @PathVariable String repoName) {

        return ResponseEntity.ok(repoService.getBranches(username, repoName));
    }
}