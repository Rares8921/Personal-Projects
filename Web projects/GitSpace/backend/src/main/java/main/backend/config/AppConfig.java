package main.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * General application bean definitions.
 * DI for utility and global configs.
 */
@Configuration
public class AppConfig {

    /**
     * Default to a local folder "git-storage" if not specified.
     */
    @Value("${git.storage.path:git-storage}")
    private String storageLocation;

    /**
     * DTO <-> Entity conversion.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean(name = "gitRootPath")
    public Path gitRootPath() throws IOException {
        Path root = Paths.get(storageLocation).toAbsolutePath().normalize();

        if (!Files.exists(root)) {
            Files.createDirectories(root);
            System.out.println("Initialized Git Storage Directory at: " + root);
        }

        return root;
    }
}