package com.example.E_Commerce.dto.auth;

import com.example.E_Commerce.model.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "First name cannot be Empty!")
    @Size(max = 10, message = "First name cannot exceed 10 characters")
    private String firstname;

    @NotBlank(message = "Last name cannot be Empty!")
    @Size(max = 10, message = "Last name cannot exceed 10 characters")
    private String lastname;

    @NotBlank(message = "Email cannot be Empty!")
    @Email(message ="Please provide a valid email!")
    private String email;

    @NotBlank(message = "Password cannot be Empty!")
    @Size(min = 8, message = "Password must contain at leat 8 characters")
    private String password;

    @NotBlank(message = "Confirm password cannot be empty!")
    private String confirmPassword;

    @NotBlank(message = "Phone cannot be empty!")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please provide a valid phone number")
    private String phone;

    private Role role;

}
