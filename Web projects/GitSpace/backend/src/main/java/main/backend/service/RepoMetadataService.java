package main.backend.service;

import jakarta.annotation.PostConstruct;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.dto.repo.FileOperationRequest;
import main.backend.dto.repo.RepositoryDto;
import main.backend.dto.repo.UpdateRepoRequest;
import main.backend.exception.BadRequestException;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.Repository;
import main.backend.model.User;
import main.backend.repository.RepositoryRepository;
import main.backend.repository.UserRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepoMetadataService {

    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final GitOperationService gitOperationService;
    private final ActivityService activityService;
    private final AccessControlService accessControlService;
    private final Path gitRootPath;
    private final ModelMapper modelMapper;
    private final String serverPort;

    @Autowired
    public RepoMetadataService(RepositoryRepository repositoryRepository,
            UserRepository userRepository,
            GitOperationService gitOperationService,
            ActivityService activityService,
            Path gitRootPath,
            ModelMapper modelMapper,
            @Value("${server.port}") String serverPort,
            AccessControlService accessControlService) {

        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.gitOperationService = gitOperationService;
        this.activityService = activityService;
        this.gitRootPath = gitRootPath;
        this.modelMapper = modelMapper;
        this.serverPort = serverPort;
        this.accessControlService = accessControlService;
    }

    @PostConstruct
    public void configureMapper() {
        Converter<Repository, RepositoryDto> repoConverter = new AbstractConverter<>() {
            @Override
            protected RepositoryDto convert(Repository src) {
                String cloneUrl = String.format("http://localhost:%s/git/%s/%s.git",
                        serverPort, src.getOwner().getUsername(), src.getName());

                return new RepositoryDto(
                        src.getId(),
                        src.getName(),
                        src.getDescription(),
                        src.isPublic(),
                        src.getDefaultBranch(),
                        src.getStarsCount(),
                        src.getForksCount(),
                        src.getOwner().getUsername(),
                        cloneUrl,
                        src.getCreatedAt(),
                        src.getUpdatedAt(),
                        src.getLicense(),
                        src.isArchived()
                );
            }
        };

        if (modelMapper.getTypeMap(Repository.class, RepositoryDto.class) == null) {
            modelMapper.createTypeMap(Repository.class, RepositoryDto.class).setConverter(repoConverter);
        }
    }

    @Transactional
    public RepositoryDto createRepo(String username, CreateRepoRequest request) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (repositoryRepository.existsByOwnerUsernameAndName(username, request.name())) {
            throw new BadRequestException("Repository " + request.name() + " already exists.");
        }

        Repository repo = new Repository();
        repo.setName(request.name());
        repo.setDescription(request.description());
        repo.setPublic(request.isPublic());
        repo.setLicense(request.license());
        repo.setOwner(owner);

        Repository savedRepo = repositoryRepository.save(repo);

        // Initializam folderul .git pe disc
        Path repoDiskPath = gitRootPath.resolve(username).resolve(request.name() + ".git");
        try {
            gitOperationService.createNewRepository(repoDiskPath);
            if (request.license() != null && !request.license().equals("None")) {
                createInitialLicenseCommit(repoDiskPath, request.license(), owner);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to init repo", e);
        }

        // LOG
        activityService.log(owner, "created repository " + request.name());

        return modelMapper.map(savedRepo, RepositoryDto.class);
    }

    public RepositoryDto getRepo(String username, String repoName) {
        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));
        return modelMapper.map(repo, RepositoryDto.class);
    }

    public Page<RepositoryDto> getTopRepositories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("starsCount").descending());

        Page<Repository> repoPage = repositoryRepository.findByIsPublicTrue(pageable);

        return repoPage.map(repo -> modelMapper.map(repo, RepositoryDto.class));
    }

    public List<RepositoryDto> getReposByOwner(String ownerUsername, User currentUser) {
        List<Repository> allRepos = repositoryRepository.findByOwnerUsername(ownerUsername);

        return allRepos.stream()
                .filter(repo -> accessControlService.canViewRepo(currentUser, repo))
                .map(r -> modelMapper.map(r, RepositoryDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public RepositoryDto updateRepo(String username, String repoName, UpdateRepoRequest request) {
        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));

        if (request.newName() != null && !request.newName().isBlank() && !request.newName().equals(repoName)) {
            if (repositoryRepository.existsByOwnerUsernameAndName(username, request.newName())) {
                throw new BadRequestException("Repo with name " + request.newName() + " already exists.");
            }

            Path oldPath = gitRootPath.resolve(username).resolve(repoName + ".git");
            Path newPath = gitRootPath.resolve(username).resolve(request.newName() + ".git");

            try {
                gitOperationService.renameRepository(oldPath, newPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to rename repo on disk", e);
            }

            repo.setName(request.newName());
        }

        if (request.isArchived() != null) {
            repo.setArchived(request.isArchived());
            String action = request.isArchived() ? "archived" : "unarchived";
            activityService.log(repo.getOwner(), action + " repository " + repoName);
        }

        if (request.description() != null) repo.setDescription(request.description());
        if (request.isPublic() != null) repo.setPublic(request.isPublic());
        if (request.defaultBranch() != null) repo.setDefaultBranch(request.defaultBranch());

        Repository updated = repositoryRepository.save(repo);
        return modelMapper.map(updated, RepositoryDto.class);
    }

    @Transactional
    public void deleteRepo(String username, String repoName) {
        Repository repo = repositoryRepository.findByOwnerUsernameAndName(username, repoName)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));

        activityService.log(repo.getOwner(), "deleted repository " + repoName);

        // Stergem folderul de pe disc
        Path diskPath = gitRootPath.resolve(username).resolve(repoName + ".git");
        try {
            gitOperationService.deleteRepository(diskPath);
        } catch (IOException e) {
            System.err.println("Warning: Could not delete repo folder: " + e.getMessage());
        }

        // Stergem din baza de date
        repositoryRepository.delete(repo);
    }

    private void createInitialLicenseCommit(Path repoDiskPath, String licenseName, User user) throws Exception {
        main.Git git = main.Git.open(repoDiskPath);

        Path licenseFile = repoDiskPath.resolve("LICENSE");
        String content = getLicenseText(licenseName);
        Files.writeString(licenseFile, content);

        String treeHash = git.writeTree(repoDiskPath);

        String authorName = user.getFullName();
        if (authorName == null || authorName.isBlank()) {
            authorName = user.getUsername();
        }

        main.domain.AuthorSignature author = new main.domain.AuthorSignature(
            authorName,
            user.getEmail(),
            java.time.ZonedDateTime.now()
        );

        main.domain.Commit initialCommit = new main.domain.Commit(
                treeHash,
                java.util.List.of(),
                author,
                author,
                "Initial commit: add " + licenseName + " license"
        );

        String commitHash = git.writeObject(initialCommit);
        git.updateRef("refs/heads/master", commitHash);
        git.checkout(git.readTree(treeHash));
    }

    private String getLicenseText(String licenseName) {
        return switch (licenseName) {
            case "MIT" -> "MIT License\n\nCopyright (c) " + java.time.Year.now() + " " + "Dacia Git User\n...";
            case "GPL-3.0" -> "GNU GENERAL PUBLIC LICENSE\nVersion 3, 29 June 2007...";
            case "Apache-2.0" -> "Apache License\nVersion 2.0, January 2004...";
            default -> "License: " + licenseName;
        };
    }

    /**
     * Lists all local branches for a given repository.
     * Corresponds to user request: GET /api/repos/{u}/{r}/branches
     */
    public List<String> getBranches(String username, String repoName) {
        if (!repositoryRepository.existsByOwnerUsernameAndName(username, repoName)) {
            throw new ResourceNotFoundException("Repository not found");
        }

        Path repoPath = gitRootPath.resolve(username).resolve(repoName + ".git");

        try {
            main.Git git = main.Git.open(repoPath);
            return git.listBranches();
        } catch (IOException e) {
            throw new RuntimeException("Failed to access git repository on disk", e);
        }
    }
}