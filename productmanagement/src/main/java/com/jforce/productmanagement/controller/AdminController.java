package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.dto.AuthResponseDto;
import com.jforce.productmanagement.dto.LoginRequestDto;
import com.jforce.productmanagement.dto.RegisterRequestDto;
import com.jforce.productmanagement.dto.UserResponseDto;
import com.jforce.productmanagement.service.AdminService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerAdmin(
            @Valid @RequestBody RegisterRequestDto request) {

        UserResponseDto response =
                adminService.registerAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginAdmin(
            @Valid @RequestBody LoginRequestDto request) {

        AuthResponseDto response = adminService.loginAdmin(request);

        return ResponseEntity.ok(response);
    }
}