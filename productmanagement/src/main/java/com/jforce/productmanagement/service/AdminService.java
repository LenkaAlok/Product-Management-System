package com.jforce.productmanagement.service;
import com.jforce.productmanagement.dto.AuthResponseDto;
import com.jforce.productmanagement.dto.LoginRequestDto;
import com.jforce.productmanagement.dto.RegisterRequestDto;
import com.jforce.productmanagement.dto.UserResponseDto;
import com.jforce.productmanagement.entity.Role;
import com.jforce.productmanagement.entity.User;
import com.jforce.productmanagement.repository.RoleRepository;
import com.jforce.productmanagement.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public AdminService(
            AuthService authService, UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.authService = authService;

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto registerAdmin(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() ->
                        new RuntimeException("ADMIN role not found"));
        User admin = new User();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());

        admin.setPassword(
                passwordEncoder.encode(request.getPassword()));

        admin.setRole(adminRole);
        User savedAdmin = userRepository.save(admin);
        UserResponseDto response = new UserResponseDto();
        response.setId(savedAdmin.getId());
        response.setName(savedAdmin.getName());
        response.setEmail(savedAdmin.getEmail());
        response.setRole(savedAdmin.getRole().getName());

        return response;
    }
    public AuthResponseDto loginAdmin(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!"ADMIN".equals(user.getRole().getName())) {
            throw new RuntimeException("This account is not an ADMIN account");
        }
        return authService.login(request);
    }
}
