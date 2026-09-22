package com.example.E_Commerce.dto.order;

import com.example.E_Commerce.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotNull
    private PaymentMethod paymentMethod;

    private String transactionId;
}
