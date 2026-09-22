package com.example.E_Commerce.controller;

import com.example.E_Commerce.dto.address.AddressRequest;
import com.example.E_Commerce.dto.address.AddressResponse;
import com.example.E_Commerce.security.CustomUserDetails;
import com.example.E_Commerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress
            (@Valid @RequestBody AddressRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(user.getUser(), request));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddress
            (@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(addressService.getAddresses(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddressById
            (@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(addressService.getAddressById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress
            (@PathVariable Long id, @Valid @RequestBody AddressRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(addressService.updateAddress(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress
            (@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        addressService.deleteAddress(id, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress
            (@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(addressService.setDefaultAddress(id, user));
    }
}
