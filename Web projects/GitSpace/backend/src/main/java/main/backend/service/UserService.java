package main.backend.service;

import main.backend.dto.repo.RepositoryDto;
import main.backend.dto.user.UserProfileDto;
import main.backend.dto.user.UserSettingsDto;
import main.backend.exception.BadRequestException;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.FollowRequest;
import main.backend.model.Role;
import main.backend.model.User;
import main.backend.model.UserFollow;
import main.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logic for user profiles, settings, and follows.
 * Corresponds to Issues 2, 4, 10, 15, 18.
 */
@Service
public class UserService {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final ActivityRepository activityRepository;
    private final SnippetRepository snippetRepository;
    private final RepositoryRepository repositoryRepository;
    private final IssueRepository issueRepository;
    private final PullRequestRepository prRepository;
    private final CommentRepository commentRepository;
    private final PullRequestReviewRepository reviewRepository;
    private final FollowRequestRepository followRequestRepository;

    private final ActivityService activityService;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserFollowRepository userFollowRepository,
                       ActivityRepository activityRepository,
                       SnippetRepository snippetRepository,
                       RepositoryRepository repositoryRepository,
                       IssueRepository issueRepository,
                       PullRequestRepository prRepository,
                       CommentRepository commentRepository,
                       PullRequestReviewRepository reviewRepository,
                       FollowRequestRepository followRequestRepository,
                       ActivityService activityService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
        this.activityRepository = activityRepository;
        this.snippetRepository = snippetRepository;
        this.repositoryRepository = repositoryRepository;
        this.issueRepository = issueRepository;
        this.prRepository = prRepository;
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.followRequestRepository = followRequestRepository;
        this.activityService = activityService;
        this.emailService = emailService;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(String username) {
        User targetUser = findByUsername(username);

        User currentUser = getCurrentUser();

        long followersCount = userFollowRepository.countByFollowing_Id(targetUser.getId());
        long followingCount = userFollowRepository.countByFollower_Id(targetUser.getId());

        boolean isFollowing = false;
        boolean isPending = false;

        if (currentUser != null && !currentUser.getId().equals(targetUser.getId())) {
            isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), targetUser.getId());
            isPending = followRequestRepository.existsByFollowerAndTarget(currentUser, targetUser);
        }

        List<RepositoryDto> repoDtos = targetUser.getRepositories() == null
                ? Collections.emptyList()
                : targetUser.getRepositories().stream()
                .map(repo -> new RepositoryDto(
                        repo.getId(),
                        repo.getName(),
                        repo.getDescription(),
                        repo.isPublic(),
                        repo.getDefaultBranch(),
                        repo.getStarsCount(),
                        repo.getForksCount(),
                        repo.getOwner().getUsername(),
                        generateCloneUrl(repo.getOwner().getUsername(), repo.getName()),
                        repo.getCreatedAt(),
                        repo.getUpdatedAt(),
                        repo.getLicense(),
                        repo.isArchived()
                ))
                .collect(Collectors.toList());

        return new UserProfileDto(
                targetUser.getId(),
                targetUser.getUsername(),
                targetUser.getFullName(),
                targetUser.getAvatarUrl(),
                targetUser.getBio(),
                targetUser.getLocation(),
                targetUser.getIsPublic(),
                isFollowing,
                isPending,
                targetUser.getWebsite(),
                targetUser.getCreatedAt(),
                followersCount,
                followingCount,
                repoDtos
        );
    }

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    private String generateCloneUrl(String username, String repoName) {
        // http://localhost:8080/git/user/repo.git
        return String.format("%s/git/%s/%s.git", baseUrl, username, repoName);
    }

    public List<User> search(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    @Transactional
    public void updateSettings(Long userId, UserSettingsDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.email() != null && !dto.email().isBlank()) user.setEmail(dto.email());
        if (dto.fullName() != null) user.setFullName(dto.fullName());
        if (dto.bio() != null) user.setBio(dto.bio());
        if (dto.location() != null) user.setLocation(dto.location());
        if (dto.website() != null) user.setWebsite(dto.website());

        userRepository.save(user);
    }

    @Transactional
    public void updateAvatar(Long userId, String fileUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setAvatarUrl(fileUrl);
        userRepository.save(user);
    }

    @Transactional
    public String follow(Long followerId, String followingUsername) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Follower not found"));

        User following = findByUsername(followingUsername);

        if (follower.getId().equals(following.getId())) {
            throw new BadRequestException("You cannot follow yourself.");
        }

        if (userFollowRepository.existsByFollowerIdAndFollowingId(follower.getId(), following.getId())) {
            throw new BadRequestException("Already following this user.");
        }

        if (follower.getRole() == Role.ROLE_ADMIN) {
            performFollow(follower, following);
            return "Followed successfully (Admin Action)";
        }

        if (following.getIsPublic()) {
            performFollow(follower, following);
            return "Followed successfully";
        }

        if (followRequestRepository.existsByFollowerAndTarget(follower, following)) {
            throw new BadRequestException("Follow request already pending.");
        }

        FollowRequest request = new FollowRequest(follower, following);
        followRequestRepository.save(request);

        emailService.sendSimpleMessage(
                following.getEmail(),
                "New Follow Request on GitSpace",
                "User " + follower.getUsername() + " wants to follow you. Check your dashboard to approve."
        );

        return "Follow request sent";
    }

    private void performFollow(User follower, User target) {
        UserFollow follow = new UserFollow();
        follow.setFollower(follower);
        follow.setFollowing(target);
        userFollowRepository.save(follow);

        activityService.log(follower, "started following " + target.getUsername());
    }

    @Transactional
    public void unfollow(Long followerId, String followingUsername) {
        User following = findByUsername(followingUsername);

        UserFollow follow = userFollowRepository.findByFollowerIdAndFollowingId(followerId, following.getId())
                .orElseThrow(() -> new BadRequestException("You are not following this user."));

        userFollowRepository.delete(follow);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userFollowRepository.deleteAllByFollower(user);
        userFollowRepository.deleteAllByFollowing(user);

        activityRepository.deleteAllByUser(user);

        snippetRepository.deleteAllByOwner(user);

        reviewRepository.deleteAllByReviewer(user);

        commentRepository.deleteAllByAuthor(user);

        issueRepository.deleteAllByAuthor(user);

        prRepository.deleteAllByAuthor(user);

        repositoryRepository.deleteAllByOwner(user);

        userRepository.delete(user);
    }

    public void removeAvatar(String username) {
        User user = findByUsername(username);
        user.setAvatarUrl(null);
        userRepository.save(user);
    }

    public void removeAvatarById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setAvatarUrl(null);
        userRepository.save(user);
    }
}
