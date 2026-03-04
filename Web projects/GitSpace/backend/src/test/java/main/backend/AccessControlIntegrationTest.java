package main.backend;

import main.backend.dto.repo.CreateRepoRequest;
import main.backend.dto.repo.RepositoryDto;
import main.backend.model.Repository;
import main.backend.model.RepositoryCollaborator;
import main.backend.model.User;
import main.backend.model.enums.CollaboratorRoleEnum;
import main.backend.model.enums.InvitationStatusEnum;
import main.backend.service.AccessControlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class AccessControlIntegrationTest extends BaseIntegrationTest {

    @Autowired private AccessControlService accessControlService;

    private final String OWNER = "owner_sec";
    private final String STRANGER = "stranger_sec";
    private final String READER = "reader_collab";
    private final String WRITER = "writer_collab";
    private final String PASS = "pass";

    @BeforeEach
    void setup() {
        createUser(OWNER);
        createUser(STRANGER);
        createUser(READER);
        createUser(WRITER);

        repoService.createRepo(OWNER, new CreateRepoRequest("public-repo", "Visible", true, "None"));
        repoService.createRepo(OWNER, new CreateRepoRequest("private-repo", "Hidden", false, "None"));

        Repository privateRepo = repositoryRepository.findByOwnerUsernameAndName(OWNER, "private-repo").get();
        User readerUser = userRepository.findByUsername(READER).get();
        User writerUser = userRepository.findByUsername(WRITER).get();
        User ownerUser = userRepository.findByUsername(OWNER).get();

        RepositoryCollaborator readerCollab = new RepositoryCollaborator();
        readerCollab.setRepository(privateRepo);
        readerCollab.setUser(readerUser);
        readerCollab.setRole(CollaboratorRoleEnum.READ);
        readerCollab.setStatus(InvitationStatusEnum.ACCEPTED);
        readerCollab.setInvitedBy(ownerUser);
        collaboratorRepository.save(readerCollab);

        RepositoryCollaborator writerCollab = new RepositoryCollaborator();
        writerCollab.setRepository(privateRepo);
        writerCollab.setUser(writerUser);
        writerCollab.setRole(CollaboratorRoleEnum.WRITE);
        writerCollab.setStatus(InvitationStatusEnum.ACCEPTED);
        writerCollab.setInvitedBy(ownerUser);
        collaboratorRepository.save(writerCollab);
    }

    @Test
    void testPublicRepoAccess() {
        ResponseEntity<RepositoryDto> response = restTemplate.exchange(
                "/api/repos/" + OWNER + "/public-repo",
                HttpMethod.GET,
                authenticatedRequest(STRANGER, null),
                RepositoryDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("public-repo", response.getBody().name());
    }

    @Test
    void testPrivateRepoAccess_ByStranger() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/repos/" + OWNER + "/private-repo",
                HttpMethod.GET,
                authenticatedRequest(STRANGER, null),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Stranger should not be able to know that private repo");
    }

    @Test
    void testPrivateRepoAccess_ByOwner() {
        ResponseEntity<RepositoryDto> response = restTemplate.exchange(
                "/api/repos/" + OWNER + "/private-repo",
                HttpMethod.GET,
                authenticatedRequest(OWNER, null),
                RepositoryDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testServiceLogicDirectly() {
        User ownerUser = userRepository.findByUsername(OWNER).get();
        User strangerUser = userRepository.findByUsername(STRANGER).get();

        var privateRepo = repositoryRepository.findByOwnerUsernameAndName(OWNER, "private-repo").get();
        var publicRepo = repositoryRepository.findByOwnerUsernameAndName(OWNER, "public-repo").get();

        // Verificari
        assertTrue(accessControlService.canViewRepo(strangerUser, publicRepo), "Stranger should view Public");
        assertFalse(accessControlService.canViewRepo(strangerUser, privateRepo), "Stranger can't view Private");
        assertTrue(accessControlService.canViewRepo(ownerUser, privateRepo), "Owner can view Private");

        // Write access
        assertFalse(accessControlService.canWriteRepo(strangerUser, publicRepo), "Stranger can't write even to Public");
        assertTrue(accessControlService.canWriteRepo(ownerUser, publicRepo), "Owner can write");
    }

    @Test
    void testPrivateRepoAccess_ByReader() {
        ResponseEntity<RepositoryDto> response = restTemplate.exchange(
                "/api/repos/" + OWNER + "/private-repo",
                HttpMethod.GET,
                authenticatedRequest(READER, null),
                RepositoryDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Reader should access private repo");
        assertNotNull(response.getBody());
        assertEquals("private-repo", response.getBody().name());
    }

    @Test
    void testPrivateRepoAccess_ByWriter() {
        ResponseEntity<RepositoryDto> response = restTemplate.exchange(
                "/api/repos/" + OWNER + "/private-repo",
                HttpMethod.GET,
                authenticatedRequest(WRITER, null),
                RepositoryDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Writer should access private repo");
        assertNotNull(response.getBody());
        assertEquals("private-repo", response.getBody().name());
    }

    @Test
    void testCollaboratorAccessLogic() {
        User readerUser = userRepository.findByUsername(READER).get();
        User writerUser = userRepository.findByUsername(WRITER).get();
        User strangerUser = userRepository.findByUsername(STRANGER).get();

        var privateRepo = repositoryRepository.findByOwnerUsernameAndName(OWNER, "private-repo").get();

        // Reader can view but not write
        assertTrue(accessControlService.canViewRepo(readerUser, privateRepo), "Reader should view private repo");
        assertFalse(accessControlService.canWriteRepo(readerUser, privateRepo), "Reader can't write");

        // Writer can both view and write
        assertTrue(accessControlService.canViewRepo(writerUser, privateRepo), "Writer should view private repo");
        assertTrue(accessControlService.canWriteRepo(writerUser, privateRepo), "Writer can write");

        // Stranger can't do anything
        assertFalse(accessControlService.canViewRepo(strangerUser, privateRepo), "Stranger can't view");
        assertFalse(accessControlService.canWriteRepo(strangerUser, privateRepo), "Stranger can't write");
    }

    @Test
    void testPendingCollaboratorNoAccess() {
        // Create a pending invitation (not accepted)
        User pendingUser = new User();
        pendingUser.setUsername("pending_user");
        pendingUser.setEmail("pending@test.com");
        pendingUser.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(pendingUser);

        Repository privateRepo = repositoryRepository.findByOwnerUsernameAndName(OWNER, "private-repo").get();
        User ownerUser = userRepository.findByUsername(OWNER).get();

        RepositoryCollaborator pendingCollab = new RepositoryCollaborator();
        pendingCollab.setRepository(privateRepo);
        pendingCollab.setUser(pendingUser);
        pendingCollab.setRole(CollaboratorRoleEnum.READ);
        pendingCollab.setStatus(InvitationStatusEnum.PENDING); // Not accepted!
        pendingCollab.setInvitedBy(ownerUser);
        collaboratorRepository.save(pendingCollab);

        // Pending user should NOT have access
        assertFalse(accessControlService.canViewRepo(pendingUser, privateRepo),
            "Pending collaborator shouldn't have access");
    }

    private void createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(username + "@sec.com");
        u.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(u);
    }
}