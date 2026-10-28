package com.jforce.productmanagement;
import com.jforce.productmanagement.entity.Role;
import com.jforce.productmanagement.repository.RoleRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeRoles(
            RoleRepository roleRepository) {

        return args -> {

            createRoleIfNotExists(roleRepository, "USER");
            createRoleIfNotExists(roleRepository, "ADMIN");
            createRoleIfNotExists(roleRepository, "SUPER_ADMIN");

            System.out.println("Default roles initialized successfully.");
        };
    }

    private void createRoleIfNotExists(
            RoleRepository roleRepository,
            String roleName) {

        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);

            System.out.println("Created role: " + roleName);
        }
    }
}
