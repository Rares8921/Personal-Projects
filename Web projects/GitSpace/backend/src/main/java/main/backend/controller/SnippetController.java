package main.backend.controller;

import main.backend.dto.snippet.SnippetDto;
import main.backend.model.User;
import main.backend.service.SnippetService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Handles Code Snippets (Gists).
 */
@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService snippetService;
    private final UserService userService;

    @Autowired
    public SnippetController(SnippetService snippetService, UserService userService) {
        this.snippetService = snippetService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createSnippet(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SnippetDto dto) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(snippetService.create(user, dto));
    }

    @GetMapping
    public ResponseEntity<?> getPublicSnippets() {
        return ResponseEntity.ok(snippetService.getPublic());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSnippet(@PathVariable Long id) {
        return ResponseEntity.ok(snippetService.get(id));
    }

}
