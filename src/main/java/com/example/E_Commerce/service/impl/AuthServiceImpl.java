package com.example.E_Commerce.service.impl;

import com.example.E_Commerce.dto.auth.UserLoginRequest;
import com.example.E_Commerce.dto.auth.UserLoginResponse;
import com.example.E_Commerce.dto.auth.UserRegisterRequest;
import com.example.E_Commerce.dto.auth.UserRegisterResponse;
import com.example.E_Commerce.dto.otp.OtpVerifyRequest;
import com.example.E_Commerce.exception.InvalidCredentialsException;
import com.example.E_Commerce.exception.InvalidOtpException;
import com.example.E_Commerce.exception.OtpExpiredException;
import com.example.E_Commerce.exception.PasswordMismatchException;
import com.example.E_Commerce.exception.UserAlreadyExistsException;
import com.example.E_Commerce.model.Cart;
import com.example.E_Commerce.model.Role;
import com.example.E_Commerce.model.User;
import com.example.E_Commerce.repository.UserRepository;
import com.example.E_Commerce.security.JwtUtils;
import com.example.E_Commerce.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public UserRegisterResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new UserAlreadyExistsException("User already exists with the provided email");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Password and confirm password doesn't match!");
        }

        Role requestedRole = request.getRole() != null ? request.getRole() : Role.BUYER;
        if (requestedRole == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot self-register as ADMIN");
        }

        String otp = generateOtp();

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .email(request.getEmail().trim().toLowerCase())
                .role(requestedRole)
                .enabled(false)
                .otpCode(otp)
                .otpExpiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        Cart cart = Cart.builder()
                .user(user)
                .totalPrice(BigDecimal.ZERO)
                .build();
        user.setCart(cart);

        User savedUser = userRepository.save(user);

        emailService.sendOtpEmail(savedUser.getEmail(), otp);

        return mapToRegisterResponse(savedUser);
    }

    @Override
    public UserRegisterResponse verifyOtp(OtpVerifyRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email"));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        if (user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired, please request a new one");
        }

        user.setEnabled(true);
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);

        User savedUser = userRepository.save(user);
        return mapToRegisterResponse(savedUser);
    }

    @Override
    public void resendOtp(String email) {
        User user = userRepository.findUserByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email"));

        if (user.getEnabled()) {
            throw new IllegalStateException("Account is already verified");
        }

        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    private String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }

    private UserRegisterResponse mapToRegisterResponse(User savedUser) {
        UserRegisterResponse response = new UserRegisterResponse();
        response.setUserId(savedUser.getId());
        response.setFirstName(savedUser.getFirstname());
        response.setLastName(savedUser.getLastname());
        response.setEmail(savedUser.getEmail());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());

        return response;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email or Password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException("Invalid Email or Password!");

        if (!user.getEnabled()) {
            throw new InvalidCredentialsException("Please verify your email before logging in");
        }

        String token = jwtUtils.generateToken(user);

        return mapToLoginResponse(user, token);
    }

    private UserLoginResponse mapToLoginResponse(User user, String token) {

        UserLoginResponse response = new UserLoginResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstname(user.getFirstname());
        response.setLastname(user.getLastname());
        response.setRole(user.getRole().name());
        response.setToken(token);

        return response;
    }
}