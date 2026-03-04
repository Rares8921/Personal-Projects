package main.backend.controller;

import main.backend.dto.comment.CommentDto;
import main.backend.dto.comment.CommentRequest;
import main.backend.dto.pr.PullRequestDto;
import main.backend.dto.pr.PullRequestFileDto;
import main.backend.model.User;
import main.backend.service.PullRequestService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Handles Pull Requests.
 */
@RestController
@RequestMapping("/api/repos/{username}/{repoName}/pulls")
public class PullRequestController {

    private final PullRequestService prService;
    private final UserService userService;

    @Autowired
    public PullRequestController(PullRequestService prService, UserService userService) {
        this.prService = prService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PullRequestDto>> listPullRequests(
            @PathVariable String username,
            @PathVariable String repoName
    ) {
        return ResponseEntity.ok(prService.list(username, repoName));
    }

    @PostMapping
    public ResponseEntity<PullRequestDto> createPullRequest(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestBody PullRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(prService.create(user, username, repoName, dto));
    }

    @GetMapping("/{prNumber}")
    public ResponseEntity<PullRequestDto> getPullRequest(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long prNumber
    ) {
        return ResponseEntity.ok(prService.get(username, repoName, prNumber));
    }

    /**
     * Get files changed in this PR (The Diff)
     */
    @GetMapping("/{prNumber}/files")
    public ResponseEntity<List<PullRequestFileDto>> getPullRequestFileChanges(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long prNumber
    ) {
        return ResponseEntity.ok(prService.getChangedFiles(username, repoName, prNumber));
    }

    /**
     * Approve or Request Changes
     * Body: { "state": "APPROVED" }
     */
    @PostMapping("/{prNumber}/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long prNumber,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        prService.addReview(user, username, repoName, prNumber, body.get("state"));
        return ResponseEntity.ok("Review added");
    }

    /**
     * Merge the PR
     */
    @PostMapping("/{prNumber}/merge")
    public ResponseEntity<?> mergePullRequest(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long prNumber
    ) {
        prService.mergePr(username, repoName, prNumber);
        return ResponseEntity.ok("PR Merged successfully");
    }

    @GetMapping("/{prNumber}/comments")
    public ResponseEntity<List<CommentDto>> getPullRequestComments(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long prNumber
    ) {
        return ResponseEntity.ok(prService.getComments(username, repoName, prNumber));
    }

    @PostMapping("/{prNumber}/comments")
    public ResponseEntity<CommentDto> addPullRequestComment(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long prNumber,
            @RequestBody CommentRequest request, // Trebuie să conțină body, filePath și lineNumber (opțional)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(prService.addComment(username, repoName, prNumber, user, request));
    }
}
