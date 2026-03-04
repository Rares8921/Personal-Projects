package main.backend.service;

import main.backend.dto.comment.CommentDto;
import main.backend.dto.comment.CommentRequest;
import main.backend.dto.pr.PullRequestDto;
import main.backend.dto.pr.PullRequestFileDto;
import main.backend.exception.BadRequestException;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.*;
import main.backend.repository.CommentRepository;
import main.backend.repository.PullRequestRepository;
import main.backend.repository.PullRequestReviewRepository;
import main.backend.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PullRequestService {

    private final PullRequestRepository prRepository;
    private final PullRequestReviewRepository reviewRepository;
    private final RepositoryRepository repositoryRepository;
    private final GitOperationService gitService;
    private final CommentRepository commentRepository;

    @Autowired
    public PullRequestService(PullRequestRepository prRepository,
                              PullRequestReviewRepository reviewRepository,
                              RepositoryRepository repositoryRepository,
                              GitOperationService gitService,
                              CommentRepository commentRepository) {
        this.prRepository = prRepository;
        this.reviewRepository = reviewRepository;
        this.repositoryRepository = repositoryRepository;
        this.gitService = gitService;
        this.commentRepository = commentRepository;
    }

    public List<PullRequestDto> list(String username, String repoName) {
        Repository repo = findRepo(username, repoName);
        return prRepository.findByRepositoryIdOrderByIdDesc(repo.getId()).stream()
                .map(pr -> mapToDto(pr, username, repoName))
                .collect(Collectors.toList());
    }

    @Transactional
    public PullRequestDto create(User author, String username, String repoName, PullRequestDto dto) {
        Repository repo = findRepo(username, repoName);

        if (dto.fromBranch().equals(dto.toBranch())) {
            throw new BadRequestException("Source and target branches must be different.");
        }

        // Fix pentru Null Pointer la prNumber
        Long maxNum = prRepository.findMaxPrNumberByRepositoryId(repo.getId());
        Long nextNum = (maxNum == null ? 0 : maxNum) + 1;

        PullRequest pr = new PullRequest();
        pr.setPrNumber(nextNum);
        pr.setTitle(dto.title());
        pr.setDescription(dto.description()); // Mapare descriere
        pr.setFromBranch(dto.fromBranch());
        pr.setToBranch(dto.toBranch());
        pr.setState("OPEN");
        pr.setRepository(repo);
        pr.setAuthor(author);
        pr.setCreatedAt(Instant.now()); // Setare data creare

        PullRequest saved = prRepository.save(pr);
        return mapToDto(saved, username, repoName);
    }

    public PullRequestDto get(String username, String repoName, Long prNumber) {
        return mapToDto(findPr(username, repoName, prNumber), username, repoName);
    }

    public List<PullRequestFileDto> getChangedFiles(String username, String repoName, Long prNumber) {
        PullRequest pr = findPr(username, repoName, prNumber);
        try {
            return gitService.getPullRequestDiff(username, repoName, pr.getToBranch(), pr.getFromBranch());
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate diff", e);
        }
    }

    @Transactional
    public void addReview(User reviewer, String username, String repoName, Long prNumber, String state) {
        PullRequest pr = findPr(username, repoName, prNumber);
        PullRequestReview review = new PullRequestReview();
        review.setPullRequest(pr);
        review.setReviewer(reviewer);
        review.setState(state);
        review.setSubmittedAt(Instant.now());
        reviewRepository.save(review);
    }

    @Transactional
    public void mergePr(String username, String repoName, Long prNumber) {
        PullRequest pr = findPr(username, repoName, prNumber);
        if (!"OPEN".equals(pr.getState())) {
            throw new BadRequestException("PR is not open");
        }
        try {
            gitService.mergePullRequest(username, repoName, pr.getToBranch(), pr.getFromBranch());
            pr.setState("MERGED");
            prRepository.save(pr);
        } catch (Exception e) {
            throw new BadRequestException("Merge failed: " + e.getMessage());
        }
    }

    private Repository findRepo(String username, String repoName) {
        return repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));
    }

    private PullRequest findPr(String username, String repoName, Long prNumber) {
        Repository repo = findRepo(username, repoName);
        return prRepository.findByRepositoryIdAndPrNumber(repo.getId(), prNumber)
                .orElseThrow(() -> new ResourceNotFoundException("PR #" + prNumber + " not found"));
    }

    private PullRequestDto mapToDto(PullRequest pr, String username, String repoName) {
        boolean hasConflicts = false;

        if ("OPEN".equals(pr.getState())) {
            try {
                hasConflicts = !gitService.checkMergeability(username, repoName, pr.getToBranch(), pr.getFromBranch());
            } catch (Exception e) {
                hasConflicts = true; // Safe fallback
            }
        }

        return new PullRequestDto(
                pr.getId(),
                pr.getPrNumber().intValue(),
                pr.getTitle(),
                pr.getDescription(),
                pr.getFromBranch(),
                pr.getToBranch(),
                pr.getAuthor().getUsername(),
                pr.getState(),
                hasConflicts,
                pr.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(String username, String repoName, Long prNumber) {
        PullRequest pr = findPr(username, repoName, prNumber);

        return commentRepository.findByPullRequestIdOrderByCreatedAtAsc(pr.getId()).stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(String username, String repoName, Long prNumber, User author, CommentRequest request) {
        PullRequest pr = findPr(username, repoName, prNumber);

        Comment comment = new Comment();
        comment.setBody(request.body());
        comment.setAuthor(author);
        comment.setPullRequest(pr);
        comment.setCreatedAt(Instant.now());

        comment.setFilePath(request.filePath());
        comment.setLineNumber(request.lineNumber());

        Comment saved = commentRepository.save(comment);
        return mapCommentToDto(saved);
    }

    private CommentDto mapCommentToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getBody(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt(),
                comment.getFilePath(),
                comment.getLineNumber()
        );
    }
}