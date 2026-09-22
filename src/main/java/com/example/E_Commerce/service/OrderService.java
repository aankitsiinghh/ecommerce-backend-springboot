package com.example.E_Commerce.service;

import com.example.E_Commerce.dto.order.*;
import com.example.E_Commerce.security.CustomUserDetails;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CustomUserDetails user, @Valid OrderRequest request);

    List<OrderResponse> getMyOrders(CustomUserDetails user);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id, CustomUserDetails user);

    OrderResponse updateOrderStatus(Long id, @Valid OrderStatusUpdateRequest request);

    PaymentResponse payForOrder(Long id, @Valid PaymentRequest request, CustomUserDetails user);

    PaymentResponse getPaymentByOrderId(Long id, CustomUserDetails user);
}
