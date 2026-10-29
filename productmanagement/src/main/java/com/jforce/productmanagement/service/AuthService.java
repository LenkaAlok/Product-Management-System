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

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    //private final AuthenticationManager authenticationManager;

    // Constructor injection
    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Register a normal USER
    public UserResponseDto register(RegisterRequestDto request) {

        // Check whether the email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Find the USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new RuntimeException("USER role not found"));

        // Create the user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Encrypt the password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        user.setRole(userRole);

        // Save the user in the database
        User savedUser = userRepository.save(user);

        // Return user details without the password
        return convertToUserResponse(savedUser);
    }

    // Login a normal USER
    public AuthResponseDto login(LoginRequestDto request) {

        // Find the user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        // Compare the entered password with the stored encoded password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        // Generate the JWT using the email and role
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().getName());

        // Create the login response
        AuthResponseDto response = new AuthResponseDto();
        response.setMessage("Login successful");
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setUser(convertToUserResponse(user));

        return response;
    }

    // Convert User entity into a safe response DTO
    private UserResponseDto convertToUserResponse(User user) {

        UserResponseDto response = new UserResponseDto();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        return response;
    }
}
