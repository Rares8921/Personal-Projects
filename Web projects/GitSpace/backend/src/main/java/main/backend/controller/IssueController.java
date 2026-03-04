package main.backend.controller;

import main.backend.dto.issue.CreateIssueRequest;
import main.backend.dto.issue.IssueCommentDto;
import main.backend.dto.issue.IssueDto;
import main.backend.model.User;
import main.backend.service.IssueService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Handles Issues.
 */
@RestController
@RequestMapping("/api/repos/{username}/{repoName}/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;

    @Autowired
    public IssueController(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<IssueDto>> listIssues(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(issueService.listIssues(username, repoName, search));
    }

    @PostMapping
    public ResponseEntity<IssueDto> createIssue(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestBody CreateIssueRequest dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(issueService.createIssue(username, repoName, user, dto));
    }

    @GetMapping("/{issueNumber}")
    public ResponseEntity<IssueDto> getIssue(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long issueNumber
    ) {
        return ResponseEntity.ok(issueService.getIssue(username, repoName, issueNumber));
    }

    @PatchMapping("/{issueNumber}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long issueNumber,
            @RequestBody Map<String, Boolean> statusUpdate // {"open": false}
    ) {
        boolean isOpen = statusUpdate.get("open");
        issueService.changeStatus(username, repoName, issueNumber, isOpen);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{issueNumber}/comments")
    public ResponseEntity<IssueCommentDto> addComment(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long issueNumber,
            @RequestBody Map<String, String> body, // {"body": "My comment"}
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(issueService.addComment(username, repoName, issueNumber, user, body.get("body")));
    }

    @GetMapping("/{issueNumber}/comments")
    public ResponseEntity<List<IssueCommentDto>> getComments(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable Long issueNumber
    ) {
        return ResponseEntity.ok(issueService.getComments(username, repoName, issueNumber));
    }
}
