package main.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.backend.model.User;
import main.backend.service.GitHttpService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Git protocol
 * URL-urile sunt standard: /git/{user}/{repo}.git/...
 */
@Controller
@RequestMapping("/git")
public class GitHttpController {

    private final GitHttpService gitService;
    private final UserService userService;

    @Autowired
    public GitHttpController(GitHttpService gitService, UserService userService) {
        this.gitService = gitService;
        this.userService = userService;
    }

    // Discovery / Handshake
    // URL: GET /git/user/repo.git/info/refs?service=git-upload-pack
    @GetMapping("/{username}/{repoName:.+}/info/refs")
    public void infoRefs(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam("service") String service,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response) throws IOException {

        // poate fi doar upload-pack sau receive-pack
        if (!"git-upload-pack".equals(service) && !"git-receive-pack".equals(service)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Service not allowed");
            return;
        }

        User currentUser = null;
        if (userDetails != null) {
            currentUser = userService.findByUsername(userDetails.getUsername());
        }

        // headerele specifice Git
        response.setContentType("application/x-" + service + "-advertisement");
        response.setHeader("Cache-Control", "no-cache");

        // Delegam catre service
        gitService.infoRefs(username, repoName, service, response.getOutputStream(), currentUser);
        response.flushBuffer();
    }

    // Data Transfer (POST)
    // URL: POST /git/user/repo.git/git-upload-pack (Clone/Pull)
    // URL: POST /git/user/repo.git/git-receive-pack (Push)
    @PostMapping("/{username}/{repoName}/{service}")
    public void serviceRpc(
            @PathVariable String username,
            @PathVariable String repoName,
            @PathVariable String service,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (!"git-upload-pack".equals(service) && !"git-receive-pack".equals(service)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Service not allowed");
            return;
        }

        User currentUser = null;
        if (userDetails != null) {
            currentUser = userService.findByUsername(userDetails.getUsername());
        }

        // headerele de raspuns
        response.setContentType("application/x-" + service + "-result");
        response.setHeader("Cache-Control", "no-cache");

        // input request <-> git output
        gitService.serviceRpc(username, repoName, service, request.getInputStream(), response.getOutputStream(), currentUser);
    }
}