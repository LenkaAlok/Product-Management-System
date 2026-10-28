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

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new RuntimeException("USER role not found in database"));
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(userRole);
        User savedUser = userRepository.save(user);

        return convertToUserResponse(savedUser);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        // Compare the submitted password with the stored BCrypt hash
        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );

        AuthResponseDto response = new AuthResponseDto();
        response.setMessage("Login successful");
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setUser(convertToUserResponse(user));

        return response;
    }

    // Convert the User entity into a safe response DTO
    private UserResponseDto convertToUserResponse(User user) {

        UserResponseDto response = new UserResponseDto();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());
        return response;
    }
}

