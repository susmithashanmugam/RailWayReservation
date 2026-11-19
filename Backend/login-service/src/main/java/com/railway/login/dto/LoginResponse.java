package com.railway.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String tokenType;

    private long expiresIn;

    private String role;
}
