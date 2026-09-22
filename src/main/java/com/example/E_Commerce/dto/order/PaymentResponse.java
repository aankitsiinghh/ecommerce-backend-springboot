package com.example.E_Commerce.dto.order;

import com.example.E_Commerce.model.PaymentMethod;
import com.example.E_Commerce.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentStatus paymentStatus;
    private Long orderId;
}
