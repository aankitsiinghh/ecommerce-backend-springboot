package com.example.E_Commerce.service;


import com.example.E_Commerce.dto.address.AddressRequest;
import com.example.E_Commerce.dto.address.AddressResponse;
import com.example.E_Commerce.model.Address;
import com.example.E_Commerce.model.User;
import com.example.E_Commerce.security.CustomUserDetails;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressResponse addAddress(User user, @Valid AddressRequest request);

    List<AddressResponse> getAddresses(CustomUserDetails user);

    AddressResponse getAddressById(Long id, CustomUserDetails user);

    AddressResponse updateAddress(Long id, @Valid AddressRequest request, CustomUserDetails user);

    void deleteAddress(Long id, CustomUserDetails user);

    AddressResponse setDefaultAddress(Long id, CustomUserDetails user);
}
