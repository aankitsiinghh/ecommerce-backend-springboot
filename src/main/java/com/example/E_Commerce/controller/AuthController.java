package com.example.E_Commerce.controller;

import com.example.E_Commerce.dto.auth.UserLoginRequest;
import com.example.E_Commerce.dto.auth.UserLoginResponse;
import com.example.E_Commerce.dto.auth.UserRegisterRequest;
import com.example.E_Commerce.dto.auth.UserRegisterResponse;
import com.example.E_Commerce.dto.otp.OtpVerifyRequest;
import com.example.E_Commerce.dto.otp.ResendOtpRequest;
import com.example.E_Commerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register
            (@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<UserRegisterResponse> verifyOtp
            (@Valid @RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp
            (@Valid @RequestBody ResendOtpRequest request) {
        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok("OTP resent successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login
            (@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
