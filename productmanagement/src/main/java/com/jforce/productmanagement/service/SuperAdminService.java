package com.jforce.productmanagement.service;
import com.jforce.productmanagement.config.JwtService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuperAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SuperAdminService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Register the initial SUPER_ADMIN account.
    @Transactional
    public UserResponseDto registerSuperAdmin(
            RegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        boolean superAdminExists = userRepository.findAll()
                .stream()
                .anyMatch(user ->
                        user.getRole() != null
                                && "SUPER_ADMIN".equals(
                                user.getRole().getName()));

        if (superAdminExists) {
            throw new IllegalStateException(
                    "Initial SUPER_ADMIN registration is closed");
        }

        Role role = roleRepository.findByName("SUPER_ADMIN")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "SUPER_ADMIN role not found in database"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        UserResponseDto response = new UserResponseDto();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole().getName());

        return response;
    }

    // Authenticate a SUPER_ADMIN.
    public AuthResponseDto loginSuperAdmin(
            LoginRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        if (user.getRole() == null
                || !"SUPER_ADMIN".equals(
                user.getRole().getName())) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "SUPER_ADMIN role required");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().getName());

        AuthResponseDto response = new AuthResponseDto();
        response.setMessage("Login successful");
        response.setToken(token);
        response.setTokenType("Bearer");

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().getName());

        response.setUser(userResponse);

        return response;
    }
    // Get all users.
    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    // Get user by ID.
    public UserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return mapToUserResponse(user);
    }


    // Delete user by ID.
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // Prevent deletion of SUPER_ADMIN.
        if ("SUPER_ADMIN".equals(user.getRole().getName())) {
            throw new IllegalArgumentException(
                    "SUPER_ADMIN account cannot be deleted");
        }

        userRepository.delete(user);
    }


    // Convert User entity into response DTO.
    private UserResponseDto mapToUserResponse(User user) {

        UserResponseDto response = new UserResponseDto();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        return response;
    }
}


