package com.example.E_Commerce.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private Long userId;
    private String email;
    private String firstname;
    private String lastname;
    private String role;
    private String token;
}
