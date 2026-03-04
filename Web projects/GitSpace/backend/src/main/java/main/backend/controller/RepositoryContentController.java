package main.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import main.backend.dto.repo.FileOperationRequest;
import main.backend.model.User;
import main.backend.model.Repository;
import main.backend.service.AccessControlService;
import main.backend.service.GitOperationService;
import main.backend.service.UserService;
import main.backend.repository.RepositoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/api/repos/{username}/{repoName}/contents")
public class RepositoryContentController {

    private final GitOperationService gitOperationService;
    private final UserService userService;
    private final RepositoryRepository repositoryRepository;
    private final AccessControlService accessControl;

    public RepositoryContentController(
            GitOperationService gitOperationService,
            UserService userService,
            RepositoryRepository repositoryRepository,
            AccessControlService accessControl) {
        this.gitOperationService = gitOperationService;
        this.userService = userService;
        this.repositoryRepository = repositoryRepository;
        this.accessControl = accessControl;
    }

    @GetMapping("/**")
    public ResponseEntity<?> getFileContent(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "master") String branch,
            @AuthenticationPrincipal UserDetails userDetails,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        Repository repo = getRepoOrThrow(username, repoName);
        User currentUser = userDetails != null ? userService.findByUsername(userDetails.getUsername()) : null;

        if (!accessControl.canViewRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found");
        }

        String fullPath = request.getRequestURI();
        String path = fullPath.substring(fullPath.indexOf("/contents/") + 10);

        try {
            byte[] content = gitOperationService.getFileContent(username, repoName, branch, path);
            return ResponseEntity.ok(Map.of(
                    "path", path,
                    "content", new String(content),
                    "encoding", "utf-8"
            ));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/**")
    public ResponseEntity<?> updateFile(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestBody FileOperationRequest dto,
            @AuthenticationPrincipal UserDetails userDetails,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        Repository repo = getRepoOrThrow(username, repoName);
        User currentUser = userService.findByUsername(userDetails.getUsername());

        if (!accessControl.canWriteRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have write access to this repository");
        }

        String fullPath = request.getRequestURI();
        String filePath = fullPath.substring(fullPath.indexOf("/contents/") + 10);

        try {
            gitOperationService.commitFile(username, repoName, filePath, dto, currentUser);
            return ResponseEntity.ok(Map.of("message", "File committed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/**")
    public ResponseEntity<?> deleteFile(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestBody FileOperationRequest dto,
            @AuthenticationPrincipal UserDetails userDetails,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        Repository repo = getRepoOrThrow(username, repoName);
        User currentUser = userService.findByUsername(userDetails.getUsername());

        if (!accessControl.canWriteRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have write access to this repository");
        }

        String fullPath = request.getRequestURI();
        String filePath = fullPath.substring(fullPath.indexOf("/contents/") + 10);

        try {
            gitOperationService.deleteFile(username, repoName, filePath, dto, currentUser);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Repository getRepoOrThrow(String username, String repoName) {
        return repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found"));
    }

    @GetMapping("/archive/{branch}.zip")
    public void downloadZip(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable String branch,
            @AuthenticationPrincipal UserDetails userDetails,
            jakarta.servlet.http.HttpServletResponse response) throws Exception {

        Repository repo = getRepoOrThrow(username, repoName);
        User currentUser = userDetails != null ? userService.findByUsername(userDetails.getUsername()) : null;

        if (!accessControl.canViewRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + repoName + "-" + branch + ".zip\"");

        gitOperationService.downloadArchive(username, repoName, branch, response.getOutputStream());
    }
}