package com.example.E_Commerce.service;

import com.example.E_Commerce.dto.auth.UserLoginRequest;
import com.example.E_Commerce.dto.auth.UserLoginResponse;
import com.example.E_Commerce.dto.auth.UserRegisterRequest;
import com.example.E_Commerce.dto.auth.UserRegisterResponse;
import com.example.E_Commerce.dto.otp.OtpVerifyRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface AuthService {
    UserLoginResponse login(@Valid UserLoginRequest request);

    UserRegisterResponse register(@Valid UserRegisterRequest request);

    UserRegisterResponse verifyOtp(OtpVerifyRequest request);

    void resendOtp(@NotBlank @Email String email);
}
