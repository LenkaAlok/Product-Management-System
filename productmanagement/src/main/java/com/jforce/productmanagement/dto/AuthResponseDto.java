package com.jforce.productmanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {

    private String message;

    private String token;

    private String tokenType;

    private UserResponseDto user;
}


