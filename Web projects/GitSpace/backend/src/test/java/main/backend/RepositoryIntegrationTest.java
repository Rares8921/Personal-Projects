package main.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.dto.repo.UpdateRepoRequest;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private final String TEST_USERNAME = "test_dev";

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setEmail("dev@dacia.ro");
        user.setPasswordHash("hash_dummy");
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void testCreateRepository_Success() throws Exception {
        String repoName = "motor-1300";
        CreateRepoRequest request = new CreateRepoRequest(repoName, "Repo pentru motorul Dacia", false, "None");

        mockMvc.perform(post("/api/repos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(repoName))
                .andExpect(jsonPath("$.ownerUsername").value(TEST_USERNAME))
                .andExpect(jsonPath("$.cloneUrlHttp").exists());

        assertTrue(repositoryRepository.findByOwnerUsernameAndName(TEST_USERNAME, repoName).isPresent(),
                "Repository should be in database");

        Path repoPath = Paths.get(serverStoragePath, TEST_USERNAME, repoName + ".git");
        Path dotGitPath = repoPath.resolve(".git");
        Path headFile = dotGitPath.resolve("HEAD");

        assertTrue(Files.exists(repoPath), "repo should exist");
        assertTrue(Files.exists(dotGitPath), ".git should exist");
        assertTrue(Files.exists(headFile), "HEAD should exist");

        System.out.println("TEST PASSED: Repository created at " + repoPath.toAbsolutePath());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void testCreateDuplicateRepository_Fail() throws Exception {
        CreateRepoRequest request = new CreateRepoRequest("cutie-viteze", "desc", false, "None");

        mockMvc.perform(post("/api/repos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/repos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void testGetUserRepositories() throws Exception {
        CreateRepoRequest req1 = new CreateRepoRequest("repo-1", "desc", false, "None");
        CreateRepoRequest req2 = new CreateRepoRequest("repo-2", "desc", false, "None");

        mockMvc.perform(post("/api/repos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req1)));
        mockMvc.perform(post("/api/repos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req2)));

        mockMvc.perform(get("/api/repos/" + TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].ownerUsername").value(TEST_USERNAME));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void testUpdateRepository_Rename() throws Exception {
        String oldName = "old-name-dacia";
        CreateRepoRequest createReq = new CreateRepoRequest(oldName, "desc", false, "None");
        mockMvc.perform(post("/api/repos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createReq)));

        String newName = "new-name-renamed-dacia";
        UpdateRepoRequest updateReq = new UpdateRepoRequest(null, null, newName, null, false);

        mockMvc.perform(put("/api/repos/" + TEST_USERNAME + "/" + oldName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));

        Path oldPath = Paths.get(serverStoragePath, TEST_USERNAME, oldName + ".git");
        Path newPath = Paths.get(serverStoragePath, TEST_USERNAME, newName + ".git");

        assertTrue(Files.notExists(oldPath), "Old folder should be deleted");
        assertTrue(Files.exists(newPath), "New folder should exist");
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void testDeleteRepository() throws Exception {
        String repoName = "repo-to-delete-dacia";
        CreateRepoRequest createReq = new CreateRepoRequest(repoName, "desc", false, "None");
        mockMvc.perform(post("/api/repos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createReq)));

        mockMvc.perform(delete("/api/repos/" + TEST_USERNAME + "/" + repoName))
                .andExpect(status().isNoContent());

        assertTrue(repositoryRepository.findByOwnerUsernameAndName(TEST_USERNAME, repoName).isEmpty(), "Repo should be gone from DB");

        Path repoPath = Paths.get(serverStoragePath, TEST_USERNAME, repoName + ".git");
        assertTrue(Files.notExists(repoPath), "Repo folder should be deleted from disk");
    }
}