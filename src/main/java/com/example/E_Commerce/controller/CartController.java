package com.example.E_Commerce.controller;

import com.example.E_Commerce.dto.cart.CartItemRequest;
import com.example.E_Commerce.dto.cart.CartResponse;
import com.example.E_Commerce.security.CustomUserDetails;
import com.example.E_Commerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CartResponse> getCart(@Valid @AuthenticationPrincipal CustomUserDetails user){
        return ResponseEntity.ok(cartService.getCart(user.getUser().getId()));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CartResponse> addItem
            (@AuthenticationPrincipal CustomUserDetails user,@Valid @RequestBody CartItemRequest request){
        return ResponseEntity.ok(cartService.addItem(user.getUser().getId(), request));
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(user.getUser().getId(), cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(user.getUser().getId(), cartItemId));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomUserDetails user) {
        cartService.clearCart(user.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
