package com.example.E_Commerce.dto.otp;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendOtpRequest {

    @NotBlank
    @Email
    private String email;
}
