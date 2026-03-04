package main.backend.config;

import main.backend.model.Role;
import main.backend.model.User;
import main.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("Admin123").isEmpty()) {
                User admin = new User();
                admin.setUsername("Admin123");
                admin.setEmail("admin@dacia.git");
                admin.setFullName("System Administrator");
                admin.setIsPublic(false);
                admin.setPasswordHash(passwordEncoder.encode("Admin123"));
                admin.setRole(Role.ROLE_ADMIN);

                userRepository.save(admin);
                System.out.println("Admin created automatically: user=admin, pass=admin123");
            }
        };
    }
}