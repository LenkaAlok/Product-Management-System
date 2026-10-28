package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.dto.AuthResponseDto;
import com.jforce.productmanagement.dto.LoginRequestDto;
import com.jforce.productmanagement.dto.RegisterRequestDto;
import com.jforce.productmanagement.dto.UserResponseDto;
import com.jforce.productmanagement.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody RegisterRequestDto request) {

        UserResponseDto response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        AuthResponseDto response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
