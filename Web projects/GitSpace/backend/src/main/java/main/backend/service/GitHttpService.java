package main.backend.service;

import main.Git;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.Repository;
import main.backend.model.User;
import main.backend.repository.RepositoryRepository;
import main.pack.PackGenerator;
import main.protocol.PacketLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHttpService {

    private final RepositoryRepository repositoryRepository;
    private final Path gitRootPath;
    private final AccessControlService accessControlService;

    @Autowired
    public GitHttpService(RepositoryRepository repositoryRepository, Path gitRootPath, AccessControlService accessControlService) {
        this.repositoryRepository = repositoryRepository;
        this.gitRootPath = gitRootPath;
        this.accessControlService = accessControlService;
    }

    /**
     * Handshake: Serverul raporteaza referintele.
     */
    public void infoRefs(String username, String repoName, String service, OutputStream out, User currentUser) throws IOException {
        Repository repo = getRepository(username, repoName);

        if (!accessControlService.canViewRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found");
        }
        if ("git-receive-pack".equals(service) && !accessControlService.canWriteRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have write access to this repository");
        }

        Path repoPath = getRepoPath(username, repoName);

        Git git;
        try {
            git = Git.open(repoPath);
        } catch (Exception e) {
            throw new IOException("Repo invalid on disk: " + repoPath, e);
        }

        PacketLine.data("# service=" + service + "\n").serialize(out);
        PacketLine.flush().serialize(out);

        String headHash = git.resolveHead();

        if (headHash != null) {
            String firstLine = headHash + " HEAD\0symref=refs/heads/master multi_ack_detailed side-band-64k agent=dacia-git/1.0";
            PacketLine.data(firstLine + "\n").serialize(out);

            String refLine = headHash + " refs/heads/master";
            PacketLine.data(refLine + "\n").serialize(out);
        }

        PacketLine.flush().serialize(out);
    }

    /**
     * Gestioneaza Upload-Pack (Clone) si Receive-Pack (Push).
     */
    public void serviceRpc(String username, String repoName, String service, InputStream requestBody, OutputStream responseBody, User currentUser) throws IOException {
        Repository repo = getRepository(username, repoName);

        if (!accessControlService.canViewRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found");
        }

        if ("git-receive-pack".equals(service) && !accessControlService.canWriteRepo(currentUser, repo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have write access to this repository");
        }

        Path repoPath = getRepoPath(username, repoName);

        if ("git-upload-pack".equals(service)) {
            handleClone(repoPath, requestBody, responseBody);
        } else if ("git-receive-pack".equals(service)) {
            handlePush(repoPath, requestBody, responseBody);
        }
    }

    private void handleClone(Path repoPath, InputStream input, OutputStream output) throws IOException {
        byte[] inputBytes = input.readAllBytes();
        String inputStr = new String(inputBytes, StandardCharsets.US_ASCII);

        String wantedHash = null;
        if (inputStr.contains("want ")) {
            int idx = inputStr.indexOf("want ");
            if (idx + 45 <= inputStr.length()) {
                wantedHash = inputStr.substring(idx + 5, idx + 45);
            }
        }

        if (wantedHash == null) {
            return;
        }

        Git git = Git.open(repoPath);
        PackGenerator generator = new PackGenerator(git);
        byte[] packBytes;
        try {
            packBytes = generator.createPack(wantedHash);
        } catch (Exception e) {
            throw new IOException("Pack generation failed", e);
        }

        PacketLine.data("NAK\n").serialize(output);
        output.write(packBytes);
        output.flush();
    }

    private void handlePush(Path repoPath, InputStream input, OutputStream output) throws IOException {
        // Format cerere: <old-id> <new-id> <ref>\0report-status ... [PACK DATA]
        ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();
        int b;
        byte[] lenBytes = input.readNBytes(4);

        while ((b = input.read()) != -1) {
            if (b == 0 || b == '\n') break;
            lineBuffer.write(b);
        }

        String commandLine = lineBuffer.toString(StandardCharsets.UTF_8);
        String[] parts = commandLine.split(" ");

        if (parts.length < 3) {
            throw new IOException("Invalid push command format");
        }

        String oldHash = parts[0];
        String newHash = parts[1];
        String refName = parts[2]; // ex: refs/heads/master

        if (b == 0) {
            while ((b = input.read()) != -1 && b != '\n');
        }

        // Citim flush packet "0000" daca exista
        byte[] potentialFlush = input.readNBytes(4);

        byte[] packData = input.readAllBytes();

        Git git = Git.open(repoPath);

        try {
            git.unpackAndApply(packData);

            git.updateRef(refName, newHash);

            PacketLine.data("unpack ok\n").serialize(output);
            PacketLine.data("ok " + refName + "\n").serialize(output);
            PacketLine.flush().serialize(output);

        } catch (Exception e) {
            e.printStackTrace();
            PacketLine.data("unpack error " + e.getMessage() + "\n").serialize(output);
            PacketLine.flush().serialize(output);
        }
    }

    private Repository getRepository(String username, String repoName) {
        String cleanName = repoName.endsWith(".git") ? repoName.substring(0, repoName.length() - 4) : repoName;
        return repositoryRepository.findByOwnerUsernameAndName(username, cleanName)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
    }

    private Path getRepoPath(String username, String repoName) {
        String cleanName = repoName.endsWith(".git") ? repoName.substring(0, repoName.length() - 4) : repoName;
        return gitRootPath.resolve(username).resolve(cleanName + ".git");
    }
}