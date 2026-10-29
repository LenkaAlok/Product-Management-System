package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.dto.AuthResponseDto;
import com.jforce.productmanagement.dto.LoginRequestDto;
import com.jforce.productmanagement.dto.RegisterRequestDto;
import com.jforce.productmanagement.dto.UserResponseDto;
import com.jforce.productmanagement.service.SuperAdminService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(
            SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    // Register the initial SUPER_ADMIN.
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerSuperAdmin(
            @Valid @RequestBody RegisterRequestDto request) {

        UserResponseDto response =
                superAdminService.registerSuperAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // SUPER_ADMIN login.
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginSuperAdmin(
            @Valid @RequestBody LoginRequestDto request) {

        AuthResponseDto response =
                superAdminService.loginSuperAdmin(request);

        return ResponseEntity.ok(response);
    }
}


