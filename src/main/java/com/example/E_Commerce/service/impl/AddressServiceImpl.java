package com.example.E_Commerce.service.impl;

import com.example.E_Commerce.dto.address.AddressRequest;
import com.example.E_Commerce.dto.address.AddressResponse;
import com.example.E_Commerce.exception.AddressNotFoundException;
import com.example.E_Commerce.model.Address;
import com.example.E_Commerce.model.User;
import com.example.E_Commerce.repository.AddressRepository;
import com.example.E_Commerce.security.CustomUserDetails;
import com.example.E_Commerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public AddressResponse addAddress(User user, AddressRequest request) {
        Address address = Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .phone(request.getPhone())
                .zipCode(request.getZipcode())
                .buyer(user)
                .build();

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetExistingDefault(user.getId());
            address.setIsDefault(true);
        } else {
            address.setIsDefault(false);
        }

        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getAddresses(CustomUserDetails user) {
        return addressRepository.findByBuyerId(user.getUser().getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AddressResponse getAddressById(Long id, CustomUserDetails user) {
        Address address = getOwnedAddress(id, user);
        return mapToResponse(address);
    }

    @Override
    public AddressResponse updateAddress(Long id, AddressRequest request, CustomUserDetails user) {
        Address address = getOwnedAddress(id, user);

        address.setStreet(request.getStreet());
        address.setPhone(request.getPhone());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipcode());
        address.setCountry(request.getCountry());

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            unsetExistingDefault(user.getUser().getId());
            address.setIsDefault(true);
        }

        Address updated = addressRepository.save(address);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAddress(Long id, CustomUserDetails user) {
        Address address = getOwnedAddress(id, user);
        addressRepository.delete(address);
    }

    @Override
    public AddressResponse setDefaultAddress(Long id, CustomUserDetails user) {
        Address address = getOwnedAddress(id, user);
        unsetExistingDefault(user.getUser().getId());
        address.setIsDefault(true);
        Address updated = addressRepository.save(address);
        return mapToResponse(updated);
    }

    private Address getOwnedAddress(Long id, CustomUserDetails user) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + id));

        if (!address.getBuyer().getId().equals(user.getUser().getId())) {
            throw new AccessDeniedException("You do not have permission to access this address");
        }
        return address;
    }

    private void unsetExistingDefault(Long id) {
        addressRepository.findByBuyerId(id).stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                .forEach(a -> {a.setIsDefault(false);
                addressRepository.save(a);
                });
    }

    private AddressResponse mapToResponse(Address savedAddress) {
        AddressResponse response = new AddressResponse();
        response.setId(savedAddress.getId());
        response.setStreet(savedAddress.getStreet());
        response.setPhone(savedAddress.getPhone());
        response.setCity(savedAddress.getCity());
        response.setState(savedAddress.getState());
        response.setZipcode(savedAddress.getZipCode());
        response.setCountry(savedAddress.getCountry());
        response.setIsDefault(savedAddress.getIsDefault());
        response.setBuyerId(savedAddress.getBuyer().getId());
        return response;
    }
}
