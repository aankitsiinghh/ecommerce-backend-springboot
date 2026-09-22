package com.example.E_Commerce.dto.order;

import com.example.E_Commerce.dto.address.AddressResponse;
import com.example.E_Commerce.model.OrderStatus;
import com.example.E_Commerce.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private Long buyerId;
    private AddressResponse shippingAddress;
    private List<OrderItemResponse> items;
}
