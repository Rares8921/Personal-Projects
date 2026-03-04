package main.backend.service;

import main.backend.dto.issue.CreateIssueRequest;
import main.backend.dto.issue.IssueCommentDto;
import main.backend.dto.issue.IssueDto;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.Comment;
import main.backend.model.Issue;
import main.backend.model.Repository;
import main.backend.model.User;
import main.backend.repository.CommentRepository;
import main.backend.repository.IssueRepository;
import main.backend.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logic for Issues.
 * Corresponds to Issues 7, 34, 40.
 */
@Service
public class IssueService {

    // TODO: Inject ActivityService

    private final IssueRepository issueRepository;
    private final RepositoryRepository repositoryRepository;
    private final CommentRepository commentRepository;
    private final ActivityService activityService;

    @Autowired
    public IssueService(IssueRepository issueRepository,
                        RepositoryRepository repositoryRepository,
                        CommentRepository commentRepository,
                        ActivityService activityService) {
        this.issueRepository = issueRepository;
        this.repositoryRepository = repositoryRepository;
        this.commentRepository = commentRepository;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<IssueDto> listIssues(String username, String repoName, String query) {
        Repository repo = findRepo(username, repoName);
        List<Issue> issues;

        if (query != null && !query.isBlank()) {
            issues = issueRepository.findByRepositoryIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(repo.getId(), query);
        } else {
            issues = issueRepository.findByRepositoryIdOrderByCreatedAtDesc(repo.getId());
        }

        return issues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IssueDto createIssue(String username, String repoName, User author, CreateIssueRequest request) {
        Repository repo = findRepo(username, repoName);

        Long maxNum = issueRepository.findMaxIssueNumberByRepositoryId(repo.getId());
        Long nextNum = (maxNum == null) ? 1L : maxNum + 1;

        Issue issue = new Issue();
        issue.setRepository(repo);
        issue.setAuthor(author);
        issue.setTitle(request.title());
        issue.setBody(request.body());
        issue.setIssueNumber(nextNum);
        issue.setOpen(true);
        issue.setCreatedAt(Instant.now());

        Issue saved = issueRepository.save(issue);
        activityService.log(author, "opened issue #" + nextNum + " in " + repoName);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public IssueDto getIssue(String username, String repoName, Long issueNumber) {
        Issue issue = findIssue(username, repoName, issueNumber);
        return mapToDto(issue);
    }

    @Transactional
    public void changeStatus(String username, String repoName, Long issueNumber, boolean isOpen) {
        Issue issue = findIssue(username, repoName, issueNumber);
        issue.setOpen(isOpen);
        issueRepository.save(issue);
    }

    @Transactional
    public IssueCommentDto addComment(String username, String repoName, Long issueNumber, User author, String body) {
        Issue issue = findIssue(username, repoName, issueNumber);

        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setAuthor(author);
        comment.setBody(body);
        comment.setCreatedAt(Instant.now());

        Comment saved = commentRepository.save(comment);

        return new IssueCommentDto(
                saved.getId(),
                saved.getBody(),
                saved.getAuthor().getUsername(),
                null, // Avatar URL placeholder
                saved.getCreatedAt(),
                null
        );
    }

    public List<IssueCommentDto> getComments(String username, String repoName, Long issueNumber) {
        Issue issue = findIssue(username, repoName, issueNumber);

        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issue.getId()).stream()
                .map(c -> new IssueCommentDto(
                        c.getId(),
                        c.getBody(),
                        c.getAuthor().getUsername(),
                        null,
                        c.getCreatedAt(),
                        null
                ))
                .collect(Collectors.toList());
    }

    // Helpers
    private Repository findRepo(String username, String repoName) {
        return repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
    }

    private Issue findIssue(String username, String repoName, Long issueNumber) {
        Repository repo = findRepo(username, repoName);
        return issueRepository.findByRepositoryIdAndIssueNumber(repo.getId(), issueNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Issue #" + issueNumber + " not found"));
    }

    private IssueDto mapToDto(Issue issue) {
        return new IssueDto(
                issue.getId(),
                issue.getIssueNumber(),
                issue.getTitle(),
                issue.getBody(),
                issue.getAuthor() != null ? issue.getAuthor().getUsername() : "Unknown",
                issue.getCreatedAt(),
                issue.isOpen()
        );
    }
}
