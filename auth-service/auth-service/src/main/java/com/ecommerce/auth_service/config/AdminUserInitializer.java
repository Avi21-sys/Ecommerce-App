package com.ecommerce.auth_service.config;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminUserInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Bean
    CommandLineRunner seedAdminUser(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return args -> {
            User existingAdmin = userRepository.findByUsername("admin");
            if (existingAdmin != null) {
                boolean needsUpdate = false;
                if (existingAdmin.getRole() == null || existingAdmin.getRole().isBlank()) {
                    existingAdmin.setRole("ADMIN");
                    needsUpdate = true;
                }
                // Check if password is not BCrypt encoded
                if (!existingAdmin.getPassword().startsWith("$2a$") &&
                    !existingAdmin.getPassword().startsWith("$2b$") &&
                    !existingAdmin.getPassword().startsWith("$2y$")) {
                    existingAdmin.setPassword(encoder.encode("admin123"));
                    needsUpdate = true;
                    logger.info("Re-encoding admin password to BCrypt format");
                }
                if (needsUpdate) {
                    userRepository.save(existingAdmin);
                    logger.info("Updated existing admin user");
                }
                return;
            }

            User admin = new User(null, "admin", encoder.encode("admin123"), "ADMIN");
            userRepository.save(admin);
            logger.info("Seeded default admin account with username=admin");
        };
    }
}
