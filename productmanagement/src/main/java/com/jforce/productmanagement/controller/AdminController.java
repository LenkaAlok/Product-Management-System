package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.dto.AuthResponseDto;
import com.jforce.productmanagement.dto.LoginRequestDto;
import com.jforce.productmanagement.dto.RegisterRequestDto;
import com.jforce.productmanagement.dto.UserResponseDto;
import com.jforce.productmanagement.service.AdminService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // Constructor injection
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Register ADMIN
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerAdmin(
            @RequestBody RegisterRequestDto request) {

        UserResponseDto response =
                adminService.registerAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Login ADMIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginAdmin(
            @RequestBody LoginRequestDto request) {

        AuthResponseDto response =
                adminService.loginAdmin(request);

        return ResponseEntity.ok(response);
    }
}

