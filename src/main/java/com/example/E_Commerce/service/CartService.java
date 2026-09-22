package com.example.E_Commerce.service;

import com.example.E_Commerce.dto.cart.CartItemRequest;
import com.example.E_Commerce.dto.cart.CartResponse;
import com.example.E_Commerce.model.User;
import jakarta.validation.Valid;

public interface CartService {

    CartResponse getCart(Long id);

    CartResponse addItem(Long id, CartItemRequest request);

    CartResponse updateItemQuantity(Long id, Long cartItemId, @Valid CartItemRequest request);

    CartResponse removeItemFromCart(Long id, Long cartItemId);

    void clearCart(Long id);
}
