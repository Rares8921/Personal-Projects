package main.backend.controller;

import main.backend.dto.FileUploadResponse;
import main.backend.model.User;
import main.backend.service.FileUploadService;
import main.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Handles file uploads.
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final UserService userService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService, UserService userService) {
        this.fileUploadService = fileUploadService;
        this.userService = userService;
    }

    /**
     * Endpoint for uploading profile avatars.
     * Corresponds to Issue 18: Profile Avatar
     */
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        String fileUrl = fileUploadService.store(file, "avatars");
        userService.updateAvatar(user.getId(), fileUrl);
        return ResponseEntity.ok(new FileUploadResponse(fileUrl));
    }

    /**
     * Endpoint for uploading attachments to issues/PRs.
     * Corresponds to Issue 14: File Upload
     */
    @PostMapping("/attachment")
    public ResponseEntity<?> uploadAttachment(
            @RequestParam("file") MultipartFile file
    ) {
        String fileUrl = fileUploadService.store(file, "attachments");
        return ResponseEntity.ok(new FileUploadResponse(fileUrl));
    }

    @GetMapping("/files/{subdir}/{filename}")
    public ResponseEntity<?> getFile(@PathVariable String subdir, @PathVariable String filename) throws IOException {
        byte[] data = fileUploadService.load(subdir, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // Sau detectie dinamica
                .body(new ByteArrayResource(data));
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteOwnAvatar(@AuthenticationPrincipal UserDetails userDetails) {
        userService.removeAvatar(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}