package main.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;


@Service
public class FileUploadService {

    @Value("${upload.path:/git-storage/uploads}")
    private String uploadPath;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String store(MultipartFile file, String subDirectory) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            // Generam nume unic
            String extension = "";
            int i = originalFilename.lastIndexOf('.');
            if (i > 0) {
                extension = originalFilename.substring(i);
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            Path targetDir = Path.of(uploadPath, subDirectory);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Path targetPath = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Returnam URL-ul public (ex: /api/upload/files/avatars/xyz.png)
            return baseUrl + "/api/upload/files/" + subDirectory + "/" + newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }

    public byte[] load(String subDirectory, String filename) throws IOException {
        Path filePath = Path.of(uploadPath, subDirectory, filename);
        return Files.readAllBytes(filePath);
    }
}
