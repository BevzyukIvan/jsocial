package com.example.jsocial.security.jwt;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
